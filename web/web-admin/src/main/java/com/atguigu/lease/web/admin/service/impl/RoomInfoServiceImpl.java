package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.RoomInfoMapper;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {
    private final GraphInfoService graphInfoService;
    private final RoomAttrValueService roomAttrValueService;
    private final RoomFacilityService roomFacilityService;
    private final RoomLabelService roomLabelService;
    private final RoomPaymentTypeService roomPaymentTypeService;
    private final RoomLeaseTermService roomLeaseTermService;
    public RoomInfoServiceImpl(RoomAttrValueService roomAttrValueService, GraphInfoService graphInfoService
    , RoomFacilityService roomFacilityService, RoomLabelService roomLabelService
    , RoomPaymentTypeService roomPaymentTypeService, RoomLeaseTermService roomLeaseTermService) {
        this.graphInfoService = graphInfoService;
        this.roomAttrValueService = roomAttrValueService;
        this.roomFacilityService = roomFacilityService;
        this.roomLabelService = roomLabelService;
        this.roomPaymentTypeService = roomPaymentTypeService;
        this.roomLeaseTermService = roomLeaseTermService;
    }

    @Override
    public void saveOrUpdateRoom(RoomSubmitVo roomSubmitVo) {
        boolean isupdate = roomSubmitVo.getId() !=null; // 是否是更新操作
        super.saveOrUpdate(roomSubmitVo); // 保存或更新房间信息
        // 若为更新操作，则先删除与Room相关的各项信息列表
        if(isupdate){
            // 先删除出房间信息外的其他信息
            // 删除图片列表
            LambdaQueryWrapper<GraphInfo> graphWrapper = new LambdaQueryWrapper<>();
            graphWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
            graphWrapper.eq(GraphInfo::getItemId, roomSubmitVo.getId());
            graphInfoService.remove(graphWrapper);
            // 删除属性信息列表
            LambdaQueryWrapper<RoomAttrValue> attrQueryMapper = new LambdaQueryWrapper<>();
            attrQueryMapper.eq(RoomAttrValue::getRoomId, roomSubmitVo.getId());
            roomAttrValueService.remove(attrQueryMapper);
            // 删除配套信息列表
            LambdaQueryWrapper<RoomFacility> facilityWrapper = new LambdaQueryWrapper<>();
            facilityWrapper.eq(RoomFacility::getRoomId, roomSubmitVo.getId());
            roomFacilityService.remove(facilityWrapper);
            // 删除标签信息列表
            LambdaQueryWrapper<RoomLabel> labelQueryWrapper = new LambdaQueryWrapper<>();
            labelQueryWrapper.eq(RoomLabel::getRoomId, roomSubmitVo.getId());
            roomLabelService.remove(labelQueryWrapper);
            // 删除支付方式列表
            LambdaQueryWrapper<RoomPaymentType> paymentWrapper = new LambdaQueryWrapper<>();
            paymentWrapper.eq(RoomPaymentType::getRoomId, roomSubmitVo.getId());
            roomPaymentTypeService.remove(paymentWrapper);
            // 删除可选租期列表
            LambdaQueryWrapper<RoomLeaseTerm> leaseTermWrapper = new LambdaQueryWrapper<>();
            leaseTermWrapper.eq(RoomLeaseTerm::getRoomId, roomSubmitVo.getId());
            roomLeaseTermService.remove(leaseTermWrapper);
        }
        // 后插入
        // 1. 插入图片列表
        List<GraphVo> graphVoList = roomSubmitVo.getGraphVoList();
        if(!CollectionUtils.isEmpty(graphVoList)){ // 非空则应该插入
            // 构造批量插入集合，作为参数传入
            ArrayList<GraphInfo> graphInfos = new ArrayList<>();
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setItemType(ItemType.ROOM);
                graphInfo.setItemId(roomSubmitVo.getId());
                graphInfo.setName(graphVo.getName());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfos.add(graphInfo);
            }
            graphInfoService.saveBatch(graphInfos);
        }
        // 2. 插入属性信息列表
        List<Long> attrValueIds = roomSubmitVo.getAttrValueIds();
        if(!CollectionUtils.isEmpty(attrValueIds)){
            // 构造批量插入集合，作为参数传入
            ArrayList<RoomAttrValue> attrValues = new ArrayList<>();
            for (Long attrValueId : attrValueIds) {
                RoomAttrValue roomAttrValue = RoomAttrValue.builder().build();
                roomAttrValue.setRoomId(roomSubmitVo.getId());
                roomAttrValue.setAttrValueId(attrValueId);
                attrValues.add(roomAttrValue);
            }
            roomAttrValueService.saveBatch(attrValues);
        }
        // 3. 插入配套信息列表
        List<Long> facilityInfoIds = roomSubmitVo.getFacilityInfoIds();
        if(!CollectionUtils.isEmpty(facilityInfoIds)){
            ArrayList<RoomFacility> roomFacilities = new ArrayList<>();
            for (Long facilityInfoId : facilityInfoIds) {
                RoomFacility roomFacility = RoomFacility.builder().build();
                roomFacility.setFacilityId(facilityInfoId);
                roomFacility.setRoomId(roomSubmitVo.getId());
                roomFacilities.add(roomFacility);
            }
            roomFacilityService.saveBatch(roomFacilities);
        }
        // 4. 插入标签信息列表
        List<Long> labelInfoIds = roomSubmitVo.getLabelInfoIds();
        if(!CollectionUtils.isEmpty(labelInfoIds)){
            ArrayList<RoomLabel> roomLabels = new ArrayList<>();
            for(Long labelInfoId : labelInfoIds){
                RoomLabel roomLabel = RoomLabel.builder().build();
                roomLabel.setLabelId(labelInfoId);
                roomLabel.setRoomId(roomSubmitVo.getId());
                roomLabels.add(roomLabel);
            }
            roomLabelService.saveBatch(roomLabels);
        }
        // 5. 插入支付方式列表
        List<Long> paymentTypeIds = roomSubmitVo.getPaymentTypeIds();
        if(!CollectionUtils.isEmpty(paymentTypeIds)){
            ArrayList<RoomPaymentType> paymentTypes = new ArrayList<>();
            for(Long paymentTypeId : paymentTypeIds){
                RoomPaymentType roomPaymentType = RoomPaymentType.builder().build();
                roomPaymentType.setPaymentTypeId(paymentTypeId);
                roomPaymentType.setRoomId(roomSubmitVo.getId());
                paymentTypes.add(roomPaymentType);
            }
            roomPaymentTypeService.saveBatch(paymentTypes);
        }
        // 6. 插入可选租期列表
        List<Long> leaseTermIds = roomSubmitVo.getLeaseTermIds();
        if(!CollectionUtils.isEmpty(leaseTermIds)){
            ArrayList<RoomLeaseTerm> leaseTerms = new ArrayList<>();
            for(Long leaseTermId : leaseTermIds){
                RoomLeaseTerm roomLeaseTerm = RoomLeaseTerm.builder().build();
                roomLeaseTerm.setLeaseTermId(leaseTermId);
                roomLeaseTerm.setRoomId(roomSubmitVo.getId());
                leaseTerms.add(roomLeaseTerm);
            }
            roomLeaseTermService.saveBatch(leaseTerms);
        }
    }
}




