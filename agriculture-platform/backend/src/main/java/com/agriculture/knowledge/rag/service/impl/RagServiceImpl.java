package com.agriculture.knowledge.rag.service.impl;

import cn.hutool.core.util.StrUtil;
import com.agriculture.knowledge.article.entity.KnowledgeArticle;
import com.agriculture.knowledge.article.mapper.KnowledgeArticleMapper;
import com.agriculture.knowledge.rag.entity.KnowledgeDocument;
import com.agriculture.knowledge.rag.entity.KnowledgeDocumentChunk;
import com.agriculture.knowledge.rag.entity.KnowledgeKeyword;
import com.agriculture.knowledge.rag.entity.KnowledgeSearchLog;
import com.agriculture.knowledge.rag.mapper.KnowledgeDocumentChunkMapper;
import com.agriculture.knowledge.rag.mapper.KnowledgeDocumentMapper;
import com.agriculture.knowledge.rag.mapper.KnowledgeKeywordMapper;
import com.agriculture.knowledge.rag.mapper.KnowledgeSearchLogMapper;
import com.agriculture.knowledge.rag.service.RagService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagServiceImpl implements RagService {

    private static final int CHUNK_SIZE = 420;
    private static final int CHUNK_OVERLAP = 80;
    private static final int MAX_TOP_K = 20;
    private static final int MAX_KEYWORDS_PER_CHUNK = 80;
    private static final Pattern TOKEN_PATTERN = Pattern.compile("[\\p{IsHan}]{2,}|[A-Za-z0-9_\\-]{2,}");
    private static final Set<String> STOP_WORDS = Set.of(
            "这个", "那个", "我们", "你们", "他们", "以及", "或者", "如果", "因为", "所以", "但是", "然后", "进行",
            "可以", "需要", "通过", "对于", "关于", "相关", "主要", "一般", "常见", "使用", "管理", "技术", "农业",
            "the", "and", "for", "with", "from", "this", "that", "into"
    );

    private final KnowledgeDocumentMapper documentMapper;
    private final KnowledgeDocumentChunkMapper chunkMapper;
    private final KnowledgeKeywordMapper keywordMapper;
    private final KnowledgeSearchLogMapper searchLogMapper;
    private final KnowledgeArticleMapper articleMapper;

    @Override
    @Transactional
    public void indexArticle(Long articleId) {
        KnowledgeArticle article = articleMapper.selectById(articleId);
        if (article == null || StrUtil.isBlank(article.getContent())) {
            return;
        }

        KnowledgeDocument document = documentMapper.selectOne(
                new LambdaQueryWrapper<KnowledgeDocument>().eq(KnowledgeDocument::getArticleId, articleId));
        if (document == null) {
            document = new KnowledgeDocument();
            document.setArticleId(articleId);
            document.setDocType("ARTICLE");
            document.setCreateTime(LocalDateTime.now());
        } else {
            chunkMapper.delete(new LambdaQueryWrapper<KnowledgeDocumentChunk>().eq(KnowledgeDocumentChunk::getDocumentId, document.getId()));
            keywordMapper.delete(new LambdaQueryWrapper<KnowledgeKeyword>().eq(KnowledgeKeyword::getDocumentId, document.getId()));
        }

        document.setTitle(article.getTitle());
        document.setSource(resolveSource(article));
        document.setStatus("INDEXED");
        document.setUpdateTime(LocalDateTime.now());
        if (document.getId() == null) {
            documentMapper.insert(document);
        } else {
            documentMapper.updateById(document);
        }

        String text = buildIndexText(article);
        List<String> chunks = splitText(text);
        for (int i = 0; i < chunks.size(); i++) {
            KnowledgeDocumentChunk chunk = new KnowledgeDocumentChunk();
            chunk.setDocumentId(document.getId());
            chunk.setChunkIndex(i);
            chunk.setContent(chunks.get(i));
            chunk.setChunkSize(chunks.get(i).length());
            chunk.setCreateTime(LocalDateTime.now());
            chunkMapper.insert(chunk);
            indexKeywords(chunk.getId(), document.getId(), chunks.get(i));
        }

        log.info("RAG indexed article: articleId={}, chunks={}", articleId, chunks.size());
    }

    @Override
    @Transactional
    public void indexAllArticles() {
        List<KnowledgeArticle> articles = articleMapper.selectList(
                new LambdaQueryWrapper<KnowledgeArticle>().eq(KnowledgeArticle::getStatus, "PUBLISHED"));
        for (KnowledgeArticle article : articles) {
            try {
                indexArticle(article.getId());
            } catch (Exception e) {
                log.warn("RAG index failed: articleId={}", article.getId(), e);
            }
        }
    }

    @Override
    public Map<String, Object> ask(String query, int topK) {
        long start = System.currentTimeMillis();
        List<Map<String, Object>> results = retrieve(query, topK);
        List<Map<String, Object>> sources = results.stream()
                .limit(Math.min(5, results.size()))
                .map(item -> {
                    Map<String, Object> source = new LinkedHashMap<>();
                    source.put("documentId", item.get("documentId"));
                    source.put("articleId", item.get("articleId"));
                    source.put("title", item.get("title"));
                    source.put("source", item.get("source"));
                    source.put("chunkIndex", item.get("chunkIndex"));
                    source.put("score", item.get("score"));
                    source.put("content", item.get("content"));
                    return source;
                })
                .collect(Collectors.toList());

        String answer = buildAnswer(query, sources);
        int spentMs = (int) (System.currentTimeMillis() - start);
        logSearch(query, sources.size(), spentMs, sources);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("query", query);
        payload.put("answer", answer);
        payload.put("sources", sources);
        payload.put("results", results);
        payload.put("spentMs", spentMs);
        return payload;
    }

    @Override
    public List<Map<String, Object>> search(String query, int topK) {
        long start = System.currentTimeMillis();
        List<Map<String, Object>> results = retrieve(query, topK);
        logSearch(query, results.size(), (int) (System.currentTimeMillis() - start), results);
        return summarizeByDocument(results, topK);
    }

    @Override
    public Map<String, Object> searchWithChunks(String query, int topK) {
        long start = System.currentTimeMillis();
        List<Map<String, Object>> results = retrieve(query, topK);
        int spentMs = (int) (System.currentTimeMillis() - start);
        logSearch(query, results.size(), spentMs, results);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("query", query);
        payload.put("results", summarizeByDocument(results, topK));
        payload.put("chunks", results);
        payload.put("spentMs", spentMs);
        return payload;
    }

    @Override
    public List<Map<String, Object>> searchByKeywords(List<String> keywords) {
        String query = keywords == null ? "" : String.join(" ", keywords);
        return search(query, 20);
    }

    private List<Map<String, Object>> retrieve(String query, int topK) {
        int limit = normalizeTopK(topK);
        List<String> terms = tokenize(query);
        if (terms.isEmpty()) {
            return List.of();
        }

        Map<Long, Double> chunkScores = new LinkedHashMap<>();
        for (String term : terms) {
            List<KnowledgeKeyword> keywords = keywordMapper.selectList(
                    new LambdaQueryWrapper<KnowledgeKeyword>().like(KnowledgeKeyword::getKeyword, term));
            for (KnowledgeKeyword keyword : keywords) {
                if (keyword.getChunkId() == null) {
                    continue;
                }
                double weight = 2.0 + safeDouble(keyword.getTfidf()) * 8.0 + safeInt(keyword.getFrequency()) * 0.25;
                if (term.equalsIgnoreCase(keyword.getKeyword())) {
                    weight += 1.5;
                }
                chunkScores.merge(keyword.getChunkId(), weight, Double::sum);
            }
        }

        List<KnowledgeDocumentChunk> directChunks = chunkMapper.selectList(
                new LambdaQueryWrapper<KnowledgeDocumentChunk>().nested(wrapper -> {
                    for (int i = 0; i < terms.size(); i++) {
                        if (i == 0) {
                            wrapper.like(KnowledgeDocumentChunk::getContent, terms.get(i));
                        } else {
                            wrapper.or().like(KnowledgeDocumentChunk::getContent, terms.get(i));
                        }
                    }
                }).last("LIMIT 200"));
        for (KnowledgeDocumentChunk chunk : directChunks) {
            double textScore = scoreText(chunk.getContent(), terms);
            if (textScore > 0) {
                chunkScores.merge(chunk.getId(), textScore, Double::sum);
            }
        }

        List<KnowledgeDocument> titleHits = documentMapper.selectList(
                new LambdaQueryWrapper<KnowledgeDocument>().nested(wrapper -> {
                    for (int i = 0; i < terms.size(); i++) {
                        if (i == 0) {
                            wrapper.like(KnowledgeDocument::getTitle, terms.get(i));
                        } else {
                            wrapper.or().like(KnowledgeDocument::getTitle, terms.get(i));
                        }
                    }
                }).last("LIMIT 50"));
        for (KnowledgeDocument document : titleHits) {
            List<KnowledgeDocumentChunk> chunks = chunkMapper.selectList(
                    new LambdaQueryWrapper<KnowledgeDocumentChunk>()
                            .eq(KnowledgeDocumentChunk::getDocumentId, document.getId())
                            .orderByAsc(KnowledgeDocumentChunk::getChunkIndex)
                            .last("LIMIT 2"));
            for (KnowledgeDocumentChunk chunk : chunks) {
                chunkScores.merge(chunk.getId(), 3.0 + scoreText(document.getTitle(), terms), Double::sum);
            }
        }

        return chunkScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> buildChunkResult(entry.getKey(), entry.getValue(), terms))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Map<String, Object> buildChunkResult(Long chunkId, double score, List<String> terms) {
        KnowledgeDocumentChunk chunk = chunkMapper.selectById(chunkId);
        if (chunk == null) {
            return null;
        }
        KnowledgeDocument document = documentMapper.selectById(chunk.getDocumentId());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("documentId", chunk.getDocumentId());
        result.put("articleId", document != null ? document.getArticleId() : null);
        result.put("title", document != null ? document.getTitle() : "");
        result.put("source", document != null ? document.getSource() : "");
        result.put("chunkId", chunk.getId());
        result.put("chunkIndex", chunk.getChunkIndex());
        result.put("content", snippet(chunk.getContent(), terms, 260));
        result.put("score", Math.round(score * 100.0) / 100.0);
        result.put("hitKeywords", terms.stream()
                .filter(term -> containsIgnoreCase(chunk.getContent(), term) || (document != null && containsIgnoreCase(document.getTitle(), term)))
                .distinct()
                .collect(Collectors.toList()));
        return result;
    }

    private List<Map<String, Object>> summarizeByDocument(List<Map<String, Object>> chunks, int topK) {
        Map<Long, List<Map<String, Object>>> grouped = chunks.stream()
                .filter(item -> item.get("documentId") != null)
                .collect(Collectors.groupingBy(
                        item -> ((Number) item.get("documentId")).longValue(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        return grouped.values().stream()
                .map(items -> {
                    Map<String, Object> first = items.get(0);
                    Map<String, Object> doc = new LinkedHashMap<>();
                    doc.put("documentId", first.get("documentId"));
                    doc.put("articleId", first.get("articleId"));
                    doc.put("title", first.get("title"));
                    doc.put("source", first.get("source"));
                    doc.put("score", items.stream().mapToDouble(item -> ((Number) item.get("score")).doubleValue()).sum());
                    doc.put("hitKeywords", items.stream()
                            .flatMap(item -> ((List<?>) item.getOrDefault("hitKeywords", List.of())).stream())
                            .distinct()
                            .collect(Collectors.toList()));
                    doc.put("chunks", items.stream()
                            .limit(3)
                            .map(item -> Map.of(
                                    "id", item.get("chunkId"),
                                    "index", item.get("chunkIndex"),
                                    "content", item.get("content"),
                                    "score", item.get("score")))
                            .collect(Collectors.toList()));
                    return doc;
                })
                .sorted((a, b) -> Double.compare(((Number) b.get("score")).doubleValue(), ((Number) a.get("score")).doubleValue()))
                .limit(normalizeTopK(topK))
                .collect(Collectors.toList());
    }

    private String buildAnswer(String query, List<Map<String, Object>> sources) {
        if (sources.isEmpty()) {
            return "当前知识库没有检索到足够相关的内容。建议先重建索引，或换用更具体的作物、病虫害、地区、季节等关键词。";
        }

        StringBuilder answer = new StringBuilder();
        answer.append("根据知识库中与“").append(query).append("”最相关的资料，可参考以下要点：\n");
        for (int i = 0; i < Math.min(3, sources.size()); i++) {
            String content = String.valueOf(sources.get(i).getOrDefault("content", ""));
            answer.append(i + 1).append(". ").append(toAnswerSentence(content)).append("\n");
        }
        answer.append("以上内容来自已入库农技文章片段，建议结合当地气候、作物生长期和田间实际症状再做处理。");
        return answer.toString();
    }

    private String toAnswerSentence(String content) {
        String normalized = normalizeText(content).replaceAll("\\s+", " ");
        if (normalized.length() <= 120) {
            return normalized;
        }
        return normalized.substring(0, 120) + "…";
    }

    private String buildIndexText(KnowledgeArticle article) {
        return String.join("\n",
                nullToEmpty(article.getTitle()),
                nullToEmpty(article.getSummary()),
                nullToEmpty(article.getCropType()),
                nullToEmpty(article.getRegion()),
                nullToEmpty(article.getSeason()),
                nullToEmpty(article.getDifficulty()),
                nullToEmpty(article.getSafetyTip()),
                nullToEmpty(article.getContent()));
    }

    private String resolveSource(KnowledgeArticle article) {
        if (StrUtil.isNotBlank(article.getSource())) {
            return article.getSource();
        }
        if (StrUtil.isNotBlank(article.getVerifiedBy())) {
            return article.getVerifiedBy();
        }
        return "平台知识库";
    }

    private List<String> splitText(String text) {
        String normalized = normalizeText(text);
        List<String> chunks = new ArrayList<>();
        for (String paragraph : normalized.split("\\n+")) {
            String p = paragraph.trim();
            if (p.isEmpty()) {
                continue;
            }
            if (p.length() <= CHUNK_SIZE) {
                chunks.add(p);
                continue;
            }
            int start = 0;
            while (start < p.length()) {
                int end = Math.min(start + CHUNK_SIZE, p.length());
                chunks.add(p.substring(start, end));
                if (end == p.length()) {
                    break;
                }
                start = Math.max(0, end - CHUNK_OVERLAP);
            }
        }
        return chunks.isEmpty() ? List.of(normalized) : chunks;
    }

    private void indexKeywords(Long chunkId, Long documentId, String text) {
        Map<String, Integer> frequencies = new LinkedHashMap<>();
        for (String token : tokenize(text)) {
            frequencies.merge(token, 1, Integer::sum);
        }

        int total = Math.max(1, frequencies.values().stream().mapToInt(Integer::intValue).sum());
        frequencies.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(MAX_KEYWORDS_PER_CHUNK)
                .forEach(entry -> {
                    KnowledgeKeyword keyword = new KnowledgeKeyword();
                    keyword.setKeyword(entry.getKey());
                    keyword.setChunkId(chunkId);
                    keyword.setDocumentId(documentId);
                    keyword.setFrequency(entry.getValue());
                    keyword.setTfidf((double) entry.getValue() / total);
                    keyword.setCreateTime(LocalDateTime.now());
                    keywordMapper.insert(keyword);
                });
    }

    private List<String> tokenize(String text) {
        String normalized = normalizeText(text).toLowerCase(Locale.ROOT);
        Set<String> tokens = new LinkedHashSet<>();
        Matcher matcher = TOKEN_PATTERN.matcher(normalized);
        while (matcher.find()) {
            String raw = matcher.group();
            if (isChinese(raw)) {
                addChineseNgrams(tokens, raw);
            } else {
                addToken(tokens, raw);
            }
        }
        return new ArrayList<>(tokens);
    }

    private void addChineseNgrams(Set<String> tokens, String text) {
        if (text.length() <= 4) {
            addToken(tokens, text);
        }
        for (int size = 2; size <= 4; size++) {
            if (text.length() < size) {
                continue;
            }
            for (int i = 0; i <= text.length() - size; i++) {
                addToken(tokens, text.substring(i, i + size));
            }
        }
    }

    private void addToken(Set<String> tokens, String token) {
        String cleaned = token.trim();
        if (cleaned.length() >= 2 && !STOP_WORDS.contains(cleaned)) {
            tokens.add(cleaned);
        }
    }

    private double scoreText(String text, List<String> terms) {
        if (text == null) {
            return 0;
        }
        double score = 0;
        String lower = text.toLowerCase(Locale.ROOT);
        for (String term : terms) {
            if (lower.contains(term.toLowerCase(Locale.ROOT))) {
                score += Math.min(5, Math.max(1, term.length() / 2.0));
            }
        }
        return score;
    }

    private String snippet(String content, List<String> terms, int maxLength) {
        String normalized = normalizeText(content);
        int start = 0;
        for (String term : terms) {
            int index = normalized.toLowerCase(Locale.ROOT).indexOf(term.toLowerCase(Locale.ROOT));
            if (index >= 0) {
                start = Math.max(0, index - 60);
                break;
            }
        }
        int end = Math.min(normalized.length(), start + maxLength);
        return (start > 0 ? "…" : "") + normalized.substring(start, end) + (end < normalized.length() ? "…" : "");
    }

    private void logSearch(String query, int resultCount, int spentMs, List<Map<String, Object>> results) {
        KnowledgeSearchLog logEntry = new KnowledgeSearchLog();
        logEntry.setQueryText(query);
        logEntry.setResultCount(resultCount);
        logEntry.setSpentMs(spentMs);
        logEntry.setTopChunkIds(results.stream()
                .map(item -> item.get("chunkId"))
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .limit(10)
                .collect(Collectors.joining(",")));
        logEntry.setCreateTime(LocalDateTime.now());
        searchLogMapper.insert(logEntry);
    }

    private String normalizeText(String text) {
        return nullToEmpty(text)
                .replaceAll("<[^>]+>", " ")
                .replace("&nbsp;", " ")
                .replaceAll("[\\r\\t]+", " ")
                .replaceAll("[ ]{2,}", " ")
                .trim();
    }

    private boolean isChinese(String text) {
        return text.chars().allMatch(ch -> Character.UnicodeScript.of(ch) == Character.UnicodeScript.HAN);
    }

    private boolean containsIgnoreCase(String text, String term) {
        return text != null && term != null && text.toLowerCase(Locale.ROOT).contains(term.toLowerCase(Locale.ROOT));
    }

    private int normalizeTopK(int topK) {
        return Math.max(1, Math.min(topK, MAX_TOP_K));
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private double safeDouble(Double value) {
        return value == null ? 0 : value;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
