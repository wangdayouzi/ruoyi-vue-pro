package cn.iocoder.yudao.module.system.sync.dto;

import lombok.Data;

/**
 * 第三方平台部门统一 DTO
 *
 * @author yudao
 */
@Data
public class ThirdPartyDeptDTO {

    /** 第三方平台部门ID */
    private String sourceDeptId;

    /** 第三方平台父部门ID（"0" 或 "1" 表示根部门） */
    private String sourceParentId;

    /** 部门名称 */
    private String name;

    /** 显示顺序 */
    private Integer sort;

}
