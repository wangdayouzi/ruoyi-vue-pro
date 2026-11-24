package cn.iocoder.yudao.module.system.api.dept;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.service.dept.DeptService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import cn.iocoder.yudao.module.system.controller.admin.dept.vo.dept.DeptListReqVO;
import java.util.Comparator;

/**
 * 部门 API 实现类
 *
 * @author 芋道源码
 */
@Service
public class DeptApiImpl implements DeptApi {

    @Resource
    private DeptService deptService;

    @Override
    public List<DeptRespDTO> getEnabledDeptList() {
        var request = new DeptListReqVO();
        request.setStatus(0);
        var departments = deptService.getDeptList(request).stream()
                .sorted(Comparator.comparing(DeptDO::getSort, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(DeptDO::getId)).toList();
        return BeanUtils.toBean(departments, DeptRespDTO.class);
    }

    @Override
    public DeptRespDTO getDept(Long id) {
        DeptDO dept = deptService.getDept(id);
        return BeanUtils.toBean(dept, DeptRespDTO.class);
    }

    @Override
    public List<DeptRespDTO> getDeptList(Collection<Long> ids) {
        List<DeptDO> depts = deptService.getDeptList(ids);
        return BeanUtils.toBean(depts, DeptRespDTO.class);
    }

    @Override
    public void validateDeptList(Collection<Long> ids) {
        deptService.validateDeptList(ids);
    }

    @Override
    public List<DeptRespDTO> getChildDeptList(Collection<Long> ids) {
        List<DeptDO> childDepts = deptService.getChildDeptList(ids);
        return BeanUtils.toBean(childDepts, DeptRespDTO.class);
    }

    @Override
    public List<DeptRespDTO> getParentDeptList(Long id) {
        List<DeptDO> parentDepts = deptService.getParentDeptList(id);
        return BeanUtils.toBean(parentDepts, DeptRespDTO.class);
    }

}
