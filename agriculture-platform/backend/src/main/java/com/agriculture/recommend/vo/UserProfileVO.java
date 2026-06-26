package com.agriculture.recommend.vo;

import lombok.Data;
import java.util.Map;

@Data
public class UserProfileVO {
    private Long userId;
    private Map<String, Double> interestTags;
    private String cropPreference;
    private String techPreference;
    private String regionPreference;
    private String profileText;
    private Boolean coldStart;
}
