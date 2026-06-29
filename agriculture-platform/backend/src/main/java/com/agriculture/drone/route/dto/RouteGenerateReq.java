package com.agriculture.drone.route.dto;

import lombok.Data;
import java.util.List;

@Data
public class RouteGenerateReq {
    private Long baseId;
    private Long greenhouseId;
    private String routeName;
    private String routeMode;       // FULL/ABNORMAL_PRIORITY/MATURE/DISEASE/DEVICE/CUSTOM
    private String scope;            // ALL/A_AREA/B_AREA/C_AREA/D_AREA
    private String routeStrategy;    // AREA_ORDER/ABNORMAL_FIRST/NEAREST/MANUAL
    private Boolean isCommon;
    private List<Long> selectedPlotIds;  // 手动选择的点位
}
