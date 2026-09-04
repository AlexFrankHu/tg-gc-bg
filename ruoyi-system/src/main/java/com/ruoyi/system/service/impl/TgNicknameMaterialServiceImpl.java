package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.TgNicknameMaterial;
import com.ruoyi.system.mapper.TgNicknameMaterialMapper;
import com.ruoyi.system.service.ITgNicknameMaterialService;

@Service
public class TgNicknameMaterialServiceImpl implements ITgNicknameMaterialService
{
    private static final int BATCH_SIZE = 500;

    @Autowired
    private TgNicknameMaterialMapper mapper;

    @Override
    public List<TgNicknameMaterial> selectList(TgNicknameMaterial query)
    {
        return mapper.selectList(query);
    }

    @Override
    public List<String> selectAllNicknames()
    {
        return mapper.selectAllNicknames();
    }

    @Override
    public int importNicknames(List<String> nicknames)
    {
        Set<String> existing = new HashSet<>(mapper.selectAllNicknames());
        List<String> toInsert = new ArrayList<>();
        for (String n : nicknames)
        {
            if (n == null)
            {
                continue;
            }
            String t = n.trim();
            if (t.isEmpty() || t.length() > 128 || !existing.add(t))
            {
                continue;
            }
            toInsert.add(t);
        }
        int count = 0;
        for (int i = 0; i < toInsert.size(); i += BATCH_SIZE)
        {
            count += mapper.batchInsert(toInsert.subList(i, Math.min(i + BATCH_SIZE, toInsert.size())));
        }
        return count;
    }

    @Override
    public int deleteByIds(Integer[] ids)
    {
        return mapper.deleteByIds(ids);
    }

    @Override
    public int deleteAll()
    {
        return mapper.deleteAll();
    }
}
