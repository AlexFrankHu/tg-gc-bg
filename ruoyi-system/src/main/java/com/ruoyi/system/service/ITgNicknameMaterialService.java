package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TgNicknameMaterial;

public interface ITgNicknameMaterialService
{
    public List<TgNicknameMaterial> selectList(TgNicknameMaterial query);

    public List<String> selectAllNicknames();

    /** 批量导入(去重, 空行忽略), 返回实际新增条数 */
    public int importNicknames(List<String> nicknames);

    public int deleteByIds(Integer[] ids);

    public int deleteAll();
}
