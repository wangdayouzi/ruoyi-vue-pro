package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class SecureDiskFormModels {
    private SecureDiskFormModels() {
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ApplicationPageReq extends PageParam {
        @Size(max = 80)
        private String keyword = "";

        @Pattern(regexp = "ALL|DRAFT|PENDING|ISSUED|VOIDED")
        private String status = "ALL";
    }

    public record Settings(List<Map<String, Object>> rules, List<Map<String, Object>> numbering) {}

    public record IssueExport(List<List<String>> columns, List<List<Object>> rows) {}

    public record PdfFile(String filename, byte[] content) {}

    public record Rule(Long id, @NotBlank @Size(max = 64) String category,
                       @NotBlank @Size(max = 128) String instrument, @NotBlank @Size(max = 512) String path,
                       @NotEmpty @Size(max = 11) List<@NotBlank String> permissions, boolean enabled) {}

    public record Account(@Positive long userId,
                          @NotBlank @Pattern(regexp = "[a-zA-Z0-9._@-]{1,100}") String nasAccount,
                          @Size(max = 100) String domainAccount) {}

    public record VoidInput(@Positive long id, @NotBlank @Size(max = 200) String reason) {}

    public record ConfirmationPreview(int year, int version, int nextSequence) {}

    public record ConfirmationRequest(@Positive long id, @Min(2000) @Max(9999) int year,
                                      @Min(1) @Max(999999999) int expectedSequence,
                                      @Min(1) @Max(999999999) int sequence) {}

    public record IssuedForm(long id, String formNo) {}

    public record Application(@NotNull UUID requestId, @NotEmpty @Size(max = 2, message = "每张申请表最多选择两个目录") List<@NotNull @Positive Long> ruleIds,
                              @Pattern(regexp = "[A-Za-z0-9_-]{0,40}") String project,
                              @Positive Long changeId, boolean submit, @NotEmpty @Size(max = 20) List<@NotNull @Positive Long> participantIds) {}
}
