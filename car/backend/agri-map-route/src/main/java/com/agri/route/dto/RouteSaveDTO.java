package com.agri.route.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 路线保存请求DTO
 */
@Data
@Schema(description = "路线保存请求")
public class RouteSaveDTO {

    @NotBlank(message = "路线名称不能为空")
    @Schema(description = "路线名称", example = "A-1号地块覆盖路线")
    private String routeName;

    @NotNull(message = "地块ID不能为空")
    @Schema(description = "地块ID")
    private Long plotId;

    @Schema(description = "农场ID")
    private Long farmId;

    @Schema(description = "路线类型", example = "COVERAGE")
    private String routeType;

    @Schema(description = "算法类型", example = "BOUSTROPHEDON")
    private String algorithm;

    @Schema(description = "航点JSON [[lng,lat],...]")
    private String waypoints;

    @Schema(description = "作业宽度(米)")
    private Double workWidth;

    @Schema(description = "转弯半径(米)")
    private Double turnRadius;

    @Schema(description = "重叠率(0-1)")
    private Double overlapRate;

    @Schema(description = "是否避障(0-否 1-是)")
    private Integer avoidObstacles;
}
