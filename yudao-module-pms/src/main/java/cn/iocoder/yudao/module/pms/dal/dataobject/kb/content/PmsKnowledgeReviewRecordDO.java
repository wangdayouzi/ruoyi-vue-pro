package cn.iocoder.yudao.module.pms.dal.dataobject.kb.content;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@TableName("pms_knowledge_review_record")
@KeySequence("pms_knowledge_review_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PmsKnowledgeReviewRecordDO extends BaseDO {
    @TableId private Long id;
    private Long documentId;
    private Long libraryId;
    /** valid, replaced, abolished or pending. */
    private String result;
    private LocalDate reviewDate;
    private String remark;
    private Long reviewerId;
}
