package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.TgProxyIp;
import com.ruoyi.system.mapper.TgProxyIpMapper;
import com.ruoyi.system.service.ITgProxyIpService;

@Service
public class TgProxyIpServiceImpl implements ITgProxyIpService
{
    @Autowired
    private TgProxyIpMapper tgProxyIpMapper;

    @Override
    public List<TgProxyIp> selectTgProxyIpList(TgProxyIp proxyIp) {
        return tgProxyIpMapper.selectTgProxyIpList(proxyIp);
    }

    @Override
    public TgProxyIp selectTgProxyIpById(Integer id) {
        return tgProxyIpMapper.selectTgProxyIpById(id);
    }

    @Override
    public int insertTgProxyIp(TgProxyIp proxyIp) {
        return tgProxyIpMapper.insertTgProxyIp(proxyIp);
    }

    @Override
    public int batchInsertTgProxyIp(List<TgProxyIp> list) {
        return tgProxyIpMapper.batchInsertTgProxyIp(list);
    }

    @Override
    public int updateTgProxyIp(TgProxyIp proxyIp) {
        return tgProxyIpMapper.updateTgProxyIp(proxyIp);
    }

    @Override
    public int deleteTgProxyIpByIds(Integer[] ids) {
        return tgProxyIpMapper.deleteTgProxyIpByIds(ids);
    }
}
