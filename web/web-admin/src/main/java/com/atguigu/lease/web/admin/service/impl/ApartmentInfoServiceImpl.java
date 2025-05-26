package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.atguigu.lease.web.admin.vo.fee.FeeValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {
    private final GraphInfoService graphInfoService;
    private final ApartmentFacilityService apartmentFacilityService;
    private final ApartmentLabelService apartmentLabelService;
    private final ApartmentFeeValueService apartmentFeeValueService;
    private final ApartmentInfoMapper apartmentInfoMapper;
    private final GraphInfoMapper graphInfoMapper;
    private final ApartmentFacilityMapper apartmentFacilityMapper;
    private final ApartmentLabelMapper apartmentLabelMapper;
    private final FeeValueMapper feeValueMapper;
    private final RoomInfoService roomInfoService;

    public ApartmentInfoServiceImpl(ApartmentFacilityMapper apartmentFacilityMapper, GraphInfoMapper graphInfoMapper,
                                    ApartmentInfoMapper apartmentInfoMapper, ApartmentFeeValueService apartmentFeeValueService,
                                    GraphInfoService graphInfoService, ApartmentFacilityService apartmentFacilityService,
                                    ApartmentLabelService apartmentLabelService, ApartmentLabelMapper apartmentLabelMapper,
                                    FeeValueMapper feeValueMapper, RoomInfoService roomInfoService) {
        this.graphInfoService = graphInfoService;
        this.apartmentLabelService = apartmentLabelService;
        this.apartmentFacilityService = apartmentFacilityService;
        this.apartmentFeeValueService = apartmentFeeValueService;
        this.apartmentInfoMapper = apartmentInfoMapper;
        this.graphInfoMapper = graphInfoMapper;
        this.apartmentFacilityMapper = apartmentFacilityMapper;
        this.apartmentLabelMapper = apartmentLabelMapper;
        this.feeValueMapper = feeValueMapper;
        this.roomInfoService = roomInfoService;
    }

    @Override
    public void saveOrUpdateApartment(ApartmentSubmitVo apartmentSubmitVo) {
        boolean isUpdate = apartmentSubmitVo.getId() != null; // 是否是更新操作
        super.saveOrUpdate(apartmentSubmitVo); // 保存或更新公寓信息
        // 若未更新操作，则先删除数据库原有的数据
        if (isUpdate) {
            // 1. 删除图片列表
            LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
            graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
            graphQueryWrapper.eq(GraphInfo::getItemId, apartmentSubmitVo.getId());
            graphInfoService.remove(graphQueryWrapper);
            // 2. 删除配套列表
            LambdaQueryWrapper<ApartmentFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
            facilityQueryWrapper.eq(ApartmentFacility::getApartmentId, apartmentSubmitVo.getId());
            apartmentFacilityService.remove(facilityQueryWrapper);
            // 3. 删除标签列表
            LambdaQueryWrapper<ApartmentLabel> labelLambdaQueryWrapper = new LambdaQueryWrapper<>();
            labelLambdaQueryWrapper.eq(ApartmentLabel::getApartmentId, apartmentSubmitVo.getId());
            apartmentLabelService.remove(labelLambdaQueryWrapper);
            // 4. 删除杂费列表
            LambdaQueryWrapper<ApartmentFeeValue> feeValueLambdaQueryWrapper = new LambdaQueryWrapper<>();
            feeValueLambdaQueryWrapper.eq(ApartmentFeeValue::getApartmentId, apartmentSubmitVo.getId());
            apartmentFeeValueService.remove(feeValueLambdaQueryWrapper);
        }
        // 后插入更新后的数据
        // 1. 插入图片列表
        List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();
        if (!CollectionUtils.isEmpty(graphVoList)) {
            ArrayList<GraphInfo> graphInfoList = new ArrayList<>();
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setItemType(ItemType.APARTMENT);
                graphInfo.setItemId(apartmentSubmitVo.getId());
                graphInfo.setName(graphVo.getName());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfoList.add(graphInfo);
            }
            graphInfoService.saveBatch(graphInfoList);
        }
        // 2. 插入配套列表
        List<Long> facilityInfoIdList = apartmentSubmitVo.getFacilityInfoIds();
        if (!CollectionUtils.isEmpty(facilityInfoIdList)) {
            ArrayList<ApartmentFacility> facilityList = new ArrayList<>();
            for (Long facilityId : facilityInfoIdList) {
                ApartmentFacility apartmentFacility = ApartmentFacility.builder().build();
                apartmentFacility.setApartmentId(apartmentSubmitVo.getId());
                apartmentFacility.setFacilityId(facilityId);
                facilityList.add(apartmentFacility);
            }
            apartmentFacilityService.saveBatch(facilityList);
        }
        // 3. 插入标签列表
        List<Long> labelIds = apartmentSubmitVo.getLabelIds();
        if (!CollectionUtils.isEmpty(labelIds)) {
            ArrayList<ApartmentLabel> labelList = new ArrayList<>();
            for (Long labelId : labelIds) {
                ApartmentLabel apartmentLabel = ApartmentLabel.builder().build();
                apartmentLabel.setApartmentId(apartmentSubmitVo.getId());
                apartmentLabel.setLabelId(labelId);
                labelList.add(apartmentLabel);
            }
            apartmentLabelService.saveBatch(labelList);
        }
        // 4. 插入杂费列表
        List<Long> feeValueIds = apartmentSubmitVo.getFeeValueIds();
        if (!CollectionUtils.isEmpty(feeValueIds)) {
            ArrayList<ApartmentFeeValue> feeValues = new ArrayList<>();
            for (Long feeValueId : feeValueIds) {
                ApartmentFeeValue apartmentFeeValue = ApartmentFeeValue.builder().build();
                apartmentFeeValue.setApartmentId(apartmentSubmitVo.getId());
                apartmentFeeValue.setFeeValueId(feeValueId);
                feeValues.add(apartmentFeeValue);
            }
            apartmentFeeValueService.saveBatch(feeValues);
        }
    }

    /**
     * 实现分页查询
     *
     * @param page
     * @param queryVo
     * @return
     */
    @Override
    public IPage<ApartmentItemVo> pageItem(IPage<ApartmentItemVo> page, ApartmentQueryVo queryVo) {
        return apartmentInfoMapper.pageItem(page, queryVo);
    }

    @Override
    public ApartmentDetailVo selectById(Long id) {
        // 1. 查询公寓信息
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(id);
        // 2. 查询公寓图片
        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, id);
        // 3. 查询公寓配套
        List<FacilityInfo> facilityInfoList = apartmentFacilityMapper.selectListByApartmentId(id);
        // 4. 查询公寓标签
        List<LabelInfo> labelInfoList = apartmentLabelMapper.selectListByApartmentId(id);
        // 5. 查询公寓杂费
        List<FeeValueVo> feeValueVoList = feeValueMapper.selectListByApartmentId(id);
        // 6. 组装返回数据
        ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();
        BeanUtils.copyProperties(apartmentInfo, apartmentDetailVo);
        apartmentDetailVo.setFacilityInfoList(facilityInfoList);
        apartmentDetailVo.setLabelInfoList(labelInfoList);
        apartmentDetailVo.setFeeValueVoList(feeValueVoList);
        apartmentDetailVo.setGraphVoList(graphVoList);

        return apartmentDetailVo;
    }

    /**
     * 由于公寓信息有杂费，标签，图片，配套等关联数据，所以删除公寓信息时，需要一并删除这些关联数据
     * @param id
     */
    @Override
    public void removeApartmentById(Long id) {
        // 删除公寓自身信息之前先得检查公寓下面是否还有房间信息，如果有，则不允许删除
        LambdaQueryWrapper<RoomInfo> roomWrapper = new LambdaQueryWrapper<>();
        roomWrapper.eq(RoomInfo::getApartmentId, id);
        long count = roomInfoService.count(roomWrapper);
        if (count > 0) {
            // 不允许删除，并抛出异常
            throw new LeaseException(ResultCodeEnum.ADMIN_APARTMRNT_DELETE_ERROR);
        }

        super.removeById(id);
        // 删除公寓图片
        LambdaQueryWrapper<GraphInfo> graphWrapper = new LambdaQueryWrapper<>();
        graphWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT); // 图片类型为公寓
        graphWrapper.eq(GraphInfo::getItemId, id);
        graphInfoService.remove(graphWrapper);
        // 删除公寓配套
        LambdaQueryWrapper<ApartmentFacility> facilityWrapper = new LambdaQueryWrapper<>();
        facilityWrapper.eq(ApartmentFacility::getApartmentId, id);
        apartmentFacilityService.remove(facilityWrapper);
        // 删除公寓标签
        LambdaQueryWrapper<ApartmentLabel> labelWrapper = new LambdaQueryWrapper<>();
        labelWrapper.eq(ApartmentLabel::getApartmentId, id);
        apartmentLabelService.remove(labelWrapper);
        // 删除公寓杂费
        LambdaQueryWrapper<ApartmentFeeValue> feeValueWrapper = new LambdaQueryWrapper<>();
        feeValueWrapper.eq(ApartmentFeeValue::getApartmentId, id);
        apartmentFeeValueService.remove(feeValueWrapper);
    }
}




