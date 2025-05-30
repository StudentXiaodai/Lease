package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.LeaseAgreementService;
import com.atguigu.lease.web.admin.vo.agreement.AgreementQueryVo;
import com.atguigu.lease.web.admin.vo.agreement.AgreementVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author liubo
 * @description 针对表【lease_agreement(租约信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement> implements LeaseAgreementService {
    private final LeaseAgreementMapper leaseAgreementMapper;
    private final ApartmentInfoMapper apartmentInfoMapper;
    private final RoomInfoMapper roomInfoMapper;
    private final PaymentTypeMapper paymentTypeMapper;
    private final LeaseTermMapper leaseTermMapper;
    public LeaseAgreementServiceImpl(LeaseAgreementMapper leaseAgreementMapper, ApartmentInfoMapper apartmentInfoMapper
    ,RoomInfoMapper roomInfoMapper, PaymentTypeMapper paymentTypeMapper, LeaseTermMapper leaseTermMapper) {
        this.leaseAgreementMapper = leaseAgreementMapper;
        this.apartmentInfoMapper = apartmentInfoMapper;
        this.roomInfoMapper = roomInfoMapper;
        this.paymentTypeMapper = paymentTypeMapper;
        this.leaseTermMapper = leaseTermMapper;
    }
    @Override
    public IPage<AgreementVo> pageAgreement(Page<AgreementVo> page, AgreementQueryVo queryVo) {
        return leaseAgreementMapper.pageAgreement(page,queryVo);
    }

    @Override
    public AgreementVo selectById(Long id) {
        LeaseAgreement leaseAgreement = leaseAgreementMapper.selectById(id);
        // 公寓信息
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(leaseAgreement.getApartmentId());
        // 房间信息
        RoomInfo roomInfo = roomInfoMapper.selectById(leaseAgreement.getRoomId());
        // 支付方式
        PaymentType paymentType = paymentTypeMapper.selectById(leaseAgreement.getPaymentTypeId());
        // 租期
        LeaseTerm leaseTerm = leaseTermMapper.selectById(leaseAgreement.getLeaseTermId());
        // 组装返回结果
        AgreementVo agreementVo = new AgreementVo();
        BeanUtils.copyProperties(leaseAgreement,agreementVo);
        agreementVo.setApartmentInfo(apartmentInfo);
        agreementVo.setRoomInfo(roomInfo);
        agreementVo.setPaymentType(paymentType);
        agreementVo.setLeaseTerm(leaseTerm);
        return agreementVo;
    }

}




