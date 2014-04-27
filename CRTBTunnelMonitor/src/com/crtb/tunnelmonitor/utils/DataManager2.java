package com.crtb.tunnelmonitor.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ICT.utils.RSACoder;
import android.os.AsyncTask;
import android.util.Log;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceTotalDataDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.DataCounter;
import com.crtb.tunnelmonitor.network.DataCounter.CounterListener;
import com.crtb.tunnelmonitor.network.PointUploadParameter;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.network.SectionUploadParamter;


public class DataManager2 {
    private static final String LOG_TAG = "UploadDataLoader";

    public static final int TUNNEL_CROSS = 0;

    public static final int SUBSIDENCE_CROSS = 1;

    public interface DataLoadListener {
        /**
         * 数据加载完毕
         * 
         * @param uploadDataList 等待上传的数据
         */
        public void done(List<UploadSheetData> uploadDataList);
    }

    public interface DataUploadListener {
        /**
         * @param success
         */
        public void done(boolean success);
    }


    private DataLoadListener mLoadListener;
    private DataUploadListener mUploadListener;

    public void loadData(int sectionType, DataLoadListener listener) {
        mLoadListener = listener;
        new DataLoadTask(sectionType).execute();
    }

    public void uploadData(List<UploadSheetData> sheetList, DataUploadListener uploadListener) {
        mUploadListener = uploadListener;
        new DataUploadTask().execute(sheetList);
    }

    private class DataLoadTask extends AsyncTask<Void, Void, List<UploadSheetData>> {
        private int mSectionType;

        public DataLoadTask(int mSectionType) {
            super();
            this.mSectionType = mSectionType;
        }

        @Override
        protected List<UploadSheetData> doInBackground(Void... params) {
            List<UploadSheetData> uploadSheetDatas = new ArrayList<UploadSheetData>();
            List<RawSheetIndex> rawSheets = null;
            switch (mSectionType) {
                case TUNNEL_CROSS:
                    // 获取隧道内断面记录单
                    rawSheets = RawSheetIndexDao.defaultDao()
                    .queryTunnelSectionRawSheetIndex();
                    break;
                case SUBSIDENCE_CROSS:
                    // 获取地表下沉断面记录单
                    rawSheets = RawSheetIndexDao.defaultDao()
                    .queryAllSubsidenceSectionRawSheetIndex();
                    break;
            }
            if (rawSheets != null && rawSheets.size() > 0) {
                for (RawSheetIndex sheet : rawSheets) {
                    UploadSheetData uploadSheetData = new UploadSheetData();
                    uploadSheetData.setRawSheet(sheet);
                    List<UploadSectionData> uploadSectionDatas = new ArrayList<UploadSectionData>();
                    switch (mSectionType) {
                        case TUNNEL_CROSS:
                            // 获取隧道内断面
                            List<TunnelCrossSectionIndex> uploadTunnelSections = getUploadTunnelSections(sheet
                                    .getCrossSectionIDs());
                            for (TunnelCrossSectionIndex section : uploadTunnelSections) {
                                UploadSectionData uploadSectionData = new UploadSectionData(
                                        mSectionType);
                                uploadSectionData.setSection(section);
                                // 该断面已上传
                                if ("2".equals(section.getInfo())) {
                                    TunnelCrossSectionExIndexDao sectionExIndexDao = TunnelCrossSectionExIndexDao
                                            .defaultDao();
                                    TunnelCrossSectionExIndex sectionExIndex = sectionExIndexDao
                                            .querySectionById(section.getID());
                                    uploadSectionData.setSectionCode(sectionExIndex.getSECTCODE());
                                }
                                // 断面的测量数据
                                List<UploadMeasureData> pointDatas = new ArrayList<UploadMeasureData>();
                                List<TunnelSettlementTotalData> unUploadPoints = getUnUploadTunnelSettlementTotalData(
                                        sheet.getID(), section.getID());
                                int measureNo = -1;
                                UploadMeasureData measureData = null;
                                for (TunnelSettlementTotalData point : unUploadPoints) {
                                    if (measureNo != point.getMEASNo()) {
                                        measureNo = point.getMEASNo();
                                        measureData = new UploadMeasureData();
                                        pointDatas.add(measureData);
                                    }
                                    measureData.addPoint(point);
                                }
                                uploadSectionData.setUnUploadMeasureDatas(pointDatas);
                                if (uploadSectionData.needUpload()) {
                                    uploadSectionDatas.add(uploadSectionData);
                                }
                            }
                            break;
                        case SUBSIDENCE_CROSS:
                            // 获取地表下沉断面
                            List<SubsidenceCrossSectionIndex> uploadSubsidenceSections = getUploadSubsidenceSections(sheet
                                    .getCrossSectionIDs());
                            for (SubsidenceCrossSectionIndex section : uploadSubsidenceSections) {
                                UploadSectionData uploadSectionData = new UploadSectionData(mSectionType);
                                uploadSectionData.setSection(section);
                                // 该断面已上传
                                // TODO：如果该断面已上传，应该单独针对已上传的断面上传新的测点
                                // if ("2".equals(section.getInfo())) {
                                // SubsidenceCrossSectionExIndexDao
                                // sectionExIndexDao =
                                // SubsidenceCrossSectionExIndexDao
                                // .defaultDao();
                                // SubsidenceCrossSectionExIndex sectionExIndex
                                // = sectionExIndexDao
                                // .querySectionById(section.getID());
                                // uploadSectionData.setSectionCode(sectionExIndex.getSECTCODE());
                                // }
                                // 断面的测量数据
                                List<UploadSubsidenceMeasureData> pointDatas = new ArrayList<UploadSubsidenceMeasureData>();
                                List<SubsidenceTotalData> unUploadPoints = getUnUploadSubsidenceTotalData(
                                        sheet.getID(), section.getID());
                                int measureNo = -1;
                                UploadSubsidenceMeasureData measureData = null;
                                for (SubsidenceTotalData point : unUploadPoints) {
                                    if (measureNo != point.getMEASNo()) {
                                        measureNo = point.getMEASNo();
                                        measureData = new UploadSubsidenceMeasureData();
                                        pointDatas.add(measureData);
                                    }
                                    measureData.addPoint(point);
                                }
                                uploadSectionData.setUnUploadSubsidenceMeasureDatas(pointDatas);
                                if (uploadSectionData.needUpload()) {
                                    uploadSectionDatas.add(uploadSectionData);
                                }
                            }
                            break;
                    }

                    uploadSheetData.setUnUpLoadSection(uploadSectionDatas);
                    uploadSheetDatas.add(uploadSheetData);
                }
            }
            return uploadSheetDatas;
        }

        @Override
        protected void onPostExecute(List<UploadSheetData> result) {
            if (mLoadListener != null) {
                mLoadListener.done(result);
            }
        }
    }

    private class DataUploadTask extends AsyncTask<List<UploadSheetData>, Void, Void> {

        @Override
        protected Void doInBackground(List<UploadSheetData>... params) {
            if (params != null && params.length > 0) {
                List<UploadSheetData> sheetDataList = params[0];
                if (sheetDataList != null && sheetDataList.size() > 0) {
                    // 上传记录单
                    final DataCounter sheetUploadCounter = new DataCounter("SheetUploadCounter",
                            sheetDataList.size(), new CounterListener() {
                        @Override
                        public void done(boolean success) {
                            if (mUploadListener != null) {
                                mUploadListener.done(success);
                            }
                        }
                    });
                    for (UploadSheetData sheetData : sheetDataList) {
                        List<UploadSectionData> sectionDataList = sheetData.getUnUploadSections();
                        if (sectionDataList != null && sectionDataList.size() > 0) {
                            DataCounter sectionUploadCounter = new DataCounter(
                                    "SectionUploadCounter", sectionDataList.size(),
                                    new CounterListener() {
                                        @Override
                                        public void done(boolean success) {
                                            sheetUploadCounter.increase(success);
                                        }
                                    });
                            for (UploadSectionData sectionData : sectionDataList) {
                                if ("1".equals(sectionData.getTunnelSection().getInfo())) {
                                    uploadSectionData(sectionData, sheetData.getRawSheet().getID(),
                                            sectionUploadCounter);
                                } else {
                                    String sectionCode = sectionData.getSectionCode();
                                    uploadSectionMeasureDatas(sectionCode, sectionData,
                                            sectionUploadCounter);
                                }
                            }
                        } else {
                            // 如果没有未上传的断面数据，则直接认为上传成功
                            sheetUploadCounter.increase(true);
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }

    }

    // 上传断面数据
    private void uploadSectionData(final UploadSectionData sectionData, final int sheetRowId,
            final DataCounter sectionUploadCounter) {
        final TunnelCrossSectionIndex section = sectionData.getTunnelSection();
        SectionUploadParamter paramter = new SectionUploadParamter();
        CrtbUtils.fillSectionParamter(section, paramter);
        CrtbWebService.getInstance().uploadSection(paramter, new RpcCallback() {
            @Override
            public void onSuccess(Object[] data) {
                final String sectionCode = (String)data[0];
                uploadSectionMeasureDatas(sectionCode, sectionData, sectionUploadCounter);
                // 将断面状态设置为已上传
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TunnelCrossSectionIndexDao dao = TunnelCrossSectionIndexDao.defaultDao();
                        section.setInfo("2");
                        dao.update(section);
                        TunnelCrossSectionExIndexDao sectionExIndexDao = TunnelCrossSectionExIndexDao
                                .defaultDao();
                        TunnelCrossSectionExIndex sectionExIndex = new TunnelCrossSectionExIndex();
                        sectionExIndex.setID(section.getID());
                        sectionExIndex.setSECTCODE(sectionCode);
                        sectionExIndexDao.insert(sectionExIndex);
                    }
                }).start();

            }

            @Override
            public void onFailed(String reason) {
                Log.d(LOG_TAG, "upload section faled: " + reason);
                sectionUploadCounter.increase(false);
            }
        });
    }

    private void uploadSectionMeasureDatas(String sectionCode, UploadSectionData sectionData,
            final DataCounter sectionUploadCounter) {
        List<UploadMeasureData> measureDataList = sectionData.getUnUploadPointDatas();
        if (measureDataList != null && measureDataList.size() > 0) {
            DataCounter pointUploadCounter = new DataCounter("MeasureDataUploadCounter",
                    measureDataList.size(), new CounterListener() {
                @Override
                public void done(boolean success) {
                    sectionUploadCounter.increase(success);
                }
            });
            for (UploadMeasureData measureData : measureDataList) {
                uploadMeasureData(sectionCode, measureData, pointUploadCounter);
            }
        } else {
            // 如果没有测点数据，则直接判断为上传断面成功
            sectionUploadCounter.increase(true);
        }
    }

    // 上传测量点数据
    private void uploadMeasureData(String sectionCode, final UploadMeasureData measureData,
            final DataCounter pointUploadCounter) {
        PointUploadParameter parameter = new PointUploadParameter();
        parameter.setSectionCode(sectionCode);
        parameter.setPointCodeList(measureData.getPointCodeList(sectionCode));
        parameter.setTunnelFaceDistance(50.0f);
        parameter.setProcedure("02");
        parameter.setMonitorModel("xxx");
        parameter.setMeasureDate(new Date());
        parameter.setPointValueList(measureData.getValueList());
        parameter.setPointCoordinateList(measureData.getCoordinateList());
        parameter.setSurveyorName("杨工");
        parameter.setSurveyorId("111");
        parameter.setRemark("yyy");
        CrtbWebService.getInstance().uploadTestResult(parameter, new RpcCallback() {

            @Override
            public void onSuccess(Object[] data) {
                CrtbWebService.getInstance().confirmSubmitData(new RpcCallback() {

                    @Override
                    public void onSuccess(Object[] data) {
                        measureData.markAsUploaded();
                        Log.d(LOG_TAG, "upload test data success.");
                        pointUploadCounter.increase(true);
                    }

                    @Override
                    public void onFailed(String reason) {
                        Log.d(LOG_TAG, "confirm test data failed: " + reason);
                        pointUploadCounter.increase(false);
                    }
                });
            }

            @Override
            public void onFailed(String reason) {
                Log.d(LOG_TAG, "upload test data failed.");
                pointUploadCounter.increase(false);
            }
        });
    }

    /**
     * 获取隧道内断面列表
     * 
     * @param sectionRowIds 断面数据库ids
     * @return
     */
    public List<TunnelCrossSectionIndex> getUploadTunnelSections(String sectionRowIds) {
        TunnelCrossSectionIndexDao dao = TunnelCrossSectionIndexDao.defaultDao();
        return dao.querySectionByIds(sectionRowIds);
    }

    /**
     * 获取地表下沉断面列表
     * 
     * @param sectionRowIds 断面数据库ids
     * @return
     */
    public List<SubsidenceCrossSectionIndex> getUploadSubsidenceSections(String sectionRowIds) {
        SubsidenceCrossSectionIndexDao dao = SubsidenceCrossSectionIndexDao.defaultDao();
        return dao.querySectionByIds(sectionRowIds);
    }

    /**
     * 获取未上传隧道内断面测点数据列表
     * 
     * @param sheetId
     * @param chainageId
     * @return
     */
    public List<TunnelSettlementTotalData> getUnUploadTunnelSettlementTotalData(int sheetId,
            int chainageId) {
        TunnelSettlementTotalDataDao pointDao = TunnelSettlementTotalDataDao.defaultDao();
        List<TunnelSettlementTotalData> unUploadPointList = new ArrayList<TunnelSettlementTotalData>();
        List<TunnelSettlementTotalData> pointList = pointDao.queryTunnelTotalDatas(sheetId,
                chainageId);
        if (pointList != null && pointList.size() > 0) {
            for (TunnelSettlementTotalData point : pointList) {
                // 1表示未上传, 2表示已上传
                if ("1".equals(point.getInfo())) {
                    unUploadPointList.add(point);
                }
            }
        }
        return unUploadPointList;
    }

    /**
     * 获取未上传地表下沉断面测点数据列表
     * 
     * @param sheetId
     * @param chainageId
     * @return
     */
    public List<SubsidenceTotalData> getUnUploadSubsidenceTotalData(int sheetId,
            int chainageId) {
        SubsidenceTotalDataDao pointDao = SubsidenceTotalDataDao.defaultDao();
        List<SubsidenceTotalData> unUploadPointList = new ArrayList<SubsidenceTotalData>();
        List<SubsidenceTotalData> pointList = pointDao.querySubsidenceTotalDatas(sheetId,chainageId);
        if (pointList != null && pointList.size() > 0) {
            for (SubsidenceTotalData point : pointList) {
                // 1表示未上传, 2表示已上传
                if ("1".equals(point.getInfo())) {
                    unUploadPointList.add(point);
                }
            }
        }
        return unUploadPointList;
    }


    public class UploadSheetData {
        private RawSheetIndex mRawSheet;
        private List<UploadSectionData> mUnUploadSections;

        UploadSheetData() {
            mUnUploadSections = new ArrayList<UploadSectionData>();
        }

        public void setRawSheet(RawSheetIndex rawSheet) {
            mRawSheet = rawSheet;
        }

        public RawSheetIndex getRawSheet() {
            return mRawSheet;
        }

        public void setUnUpLoadSection(List<UploadSectionData> unUploadSections) {
            mUnUploadSections = unUploadSections;
        }

        public List<UploadSectionData> getUnUploadSections() {
            return mUnUploadSections;
        }

        public boolean needUpload() {
            boolean result = false;
            if (mUnUploadSections != null && mUnUploadSections.size() > 0) {
                result = true;
            }
            return result;
        }
    }

    public class UploadSectionData {
        private TunnelCrossSectionIndex mTunnelSection;
        private SubsidenceCrossSectionIndex mSubsidenceSection;
        private String mSectionCode;
        private List<UploadMeasureData> mUnUploadMeasureDatas;

        private List<UploadSubsidenceMeasureData> mUnUploadSubsidenceMeasureDatas;

        private int mSectionType;

        public UploadSectionData(int mSectionType) {
            this.mSectionType = mSectionType;
        }

        public void setSection(TunnelCrossSectionIndex section) {
            mTunnelSection = section;
        }

        public void setSection(SubsidenceCrossSectionIndex section) {
            mSubsidenceSection = section;
        }

        public TunnelCrossSectionIndex getTunnelSection() {
            return mTunnelSection;
        }

        public SubsidenceCrossSectionIndex getSubsidenceSection() {
            return mSubsidenceSection;
        }

        public void setSectionCode(String sectionCode) {
            mSectionCode = sectionCode;
        }

        public String getSectionCode() {
            return mSectionCode;
        }

        public void setUnUploadMeasureDatas(List<UploadMeasureData> unUploadPointDatas) {
            mUnUploadMeasureDatas = unUploadPointDatas;
        }

        public void setUnUploadSubsidenceMeasureDatas(
                List<UploadSubsidenceMeasureData> unUploadPointDatas) {
            mUnUploadSubsidenceMeasureDatas = unUploadPointDatas;
        }

        public List<UploadMeasureData> getUnUploadPointDatas() {
            return mUnUploadMeasureDatas;
        }

        public List<UploadSubsidenceMeasureData> getUnUploadSusidencePointDatas() {
            return mUnUploadSubsidenceMeasureDatas;
        }

        public boolean needUpload() {
            boolean result = false;
            switch (mSectionType) {
                case TUNNEL_CROSS:
                    // 隧道内断面是否已上传
                    if ("1".equals(mTunnelSection.getInfo())) {
                        result = true;
                    }
                    if (mUnUploadMeasureDatas != null && mUnUploadMeasureDatas.size() > 0) {
                        result = true;
                    }
                    break;
                case SUBSIDENCE_CROSS:
                    // 地表下沉断面是否已上传
                    if ("1".equals(mSubsidenceSection.getInfo())) {
                        result = true;
                    }
                    if (mUnUploadSubsidenceMeasureDatas != null
                            && mUnUploadSubsidenceMeasureDatas.size() > 0) {
                        result = true;
                    }
                    break;
            }
            return result;
        }

    }

    public class UploadMeasureData {
        private List<TunnelSettlementTotalData> mMeasurePoints = new ArrayList<TunnelSettlementTotalData>();

        public void addPoint(TunnelSettlementTotalData point) {
            mMeasurePoints.add(point);
        }

        public List<TunnelSettlementTotalData> getPoints() {
            return mMeasurePoints;
        }

        public TunnelSettlementTotalData getPointByType(String type) {
            TunnelSettlementTotalData result = null;
            for (TunnelSettlementTotalData point : mMeasurePoints) {
                if (type.equals(point.getPntType())) {
                    result = point;
                }
            }
            return result;
        }

        public String getPointCodeList(String sectionCode) {
            String pointCodeList = "";
            final int pointCount = mMeasurePoints.size();
            switch (pointCount) {
                // 全断面法
                case 3:
                    pointCodeList = sectionCode + "GD01" + "/" + sectionCode + "SL01" + "#"
                            + sectionCode + "SL02";
                    break;
                    // 台阶法
                case 5:
                    pointCodeList = sectionCode + "GD01" + "/" + sectionCode + "SL01" + "#"
                            + sectionCode + "SL02" + "/" + sectionCode + "SL03" + "#" + sectionCode
                            + "SL04";
                    break;
                    // 三台阶法或双侧壁法
                case 7:
                    pointCodeList = sectionCode + "GD01" + "/" + sectionCode + "SL01" + "#"
                            + sectionCode + "SL02" + "/" + sectionCode + "SL03" + "#" + sectionCode
                            + "SL04" + "/" + sectionCode + "SL05" + "#" + sectionCode + "SL06";
                    break;
                default:
                    Log.d(LOG_TAG, "未知的开挖方法: 测点数目=" + pointCount);
                    break;
            }
            return pointCodeList;
        }

        public String getCoordinateList() {
            String coordinateList = "";
            final int pointCount = mMeasurePoints.size();
            TunnelSettlementTotalData pointA, pointS1_1, pointS1_2, pointS2_1, pointS2_2, pointS3_1, pointS3_2;
            String A, S1_1, S1_2, S2_1, S2_2, S3_1, S3_2, coordinate;
            switch (pointCount) {
                // 全断面法
                case 3:
                    pointA = getPointByType("A");
                    pointS1_1 = getPointByType("S1-1");
                    pointS1_2 = getPointByType("S1-2");
                    A = pointA.getCoordinate().replace(",", "#");
                    S1_1 = pointS1_1.getCoordinate().replace(",", "#");
                    S1_2 = pointS1_2.getCoordinate().replace(",", "#");
                    coordinate = A + "/" + S1_1 + "#" + S1_2;
                    coordinateList = RSACoder.encnryptDes(coordinate, Constant.testDeskey);
                    break;
                    // 台阶法
                case 5:
                    pointA = getPointByType("A");
                    pointS1_1 = getPointByType("S1-1");
                    pointS1_2 = getPointByType("S1-2");
                    pointS2_1 = getPointByType("S2-1");
                    pointS2_2 = getPointByType("S2-2");
                    A = pointA.getCoordinate().replace(",", "#");
                    S1_1 = pointS1_1.getCoordinate().replace(",", "#");
                    S1_2 = pointS1_2.getCoordinate().replace(",", "#");
                    S2_1 = pointS2_1.getCoordinate().replace(",", "#");
                    S2_2 = pointS2_2.getCoordinate().replace(",", "#");
                    coordinate = A + "/" + S1_1 + "#" + S1_2 + "/" + S2_1 + "#" + S2_2;
                    coordinateList = RSACoder.encnryptDes(coordinate, Constant.testDeskey);
                    break;
                    // 三台阶法或双侧壁法
                case 7:
                    pointA = getPointByType("A");
                    pointS1_1 = getPointByType("S1-1");
                    pointS1_2 = getPointByType("S1-2");
                    pointS2_1 = getPointByType("S2-1");
                    pointS2_2 = getPointByType("S2-2");
                    pointS3_1 = getPointByType("S3-1");
                    pointS3_2 = getPointByType("S3-2");
                    A = pointA.getCoordinate().replace(",", "#");
                    S1_1 = pointS1_1.getCoordinate().replace(",", "#");
                    S1_2 = pointS1_2.getCoordinate().replace(",", "#");
                    S2_1 = pointS2_1.getCoordinate().replace(",", "#");
                    S2_2 = pointS2_2.getCoordinate().replace(",", "#");
                    S3_1 = pointS3_1.getCoordinate().replace(",", "#");
                    S3_2 = pointS3_2.getCoordinate().replace(",", "#");
                    coordinate = A + "/" + S1_1 + "#" + S1_2 + "/" + S2_1 + "#" + S2_2 + "/" + S3_1
                            + "#" + S3_2;
                    coordinateList = RSACoder.encnryptDes(coordinate, Constant.testDeskey);
                    break;
                default:
                    Log.d(LOG_TAG, "未知的开挖方法: 测点数目=" + pointCount);
                    break;
            }
            return coordinateList;
        }

        public String getValueList() {
            String valueList = "";
            final int pointCount = mMeasurePoints.size();
            TunnelSettlementTotalData pointA, pointS1_1, pointS1_2, pointS2_1, pointS2_2, pointS3_1, pointS3_2;
            String[] cA;
            switch (pointCount) {
                // 全断面法
                case 3:
                    pointA = getPointByType("A");
                    pointS1_1 = getPointByType("S1-1");
                    pointS1_2 = getPointByType("S1-2");
                    cA = pointA.getCoordinate().split(",");
                    valueList = cA[2] + "/" + AlertUtils.getLineLength(pointS1_1, pointS1_2);
                    break;
                    // 台阶法
                case 5:
                    pointA = getPointByType("A");
                    pointS1_1 = getPointByType("S1-1");
                    pointS1_2 = getPointByType("S1-2");
                    pointS2_1 = getPointByType("S2-1");
                    pointS2_2 = getPointByType("S2-2");
                    cA = pointA.getCoordinate().split(",");
                    valueList = cA[2] + "/" + AlertUtils.getLineLength(pointS1_1, pointS1_2) + "/"
                            + AlertUtils.getLineLength(pointS2_1, pointS2_2);
                    break;
                    // 三台阶法或双侧壁法
                case 7:
                    pointA = getPointByType("A");
                    pointS1_1 = getPointByType("S1-1");
                    pointS1_2 = getPointByType("S1-2");
                    pointS2_1 = getPointByType("S2-1");
                    pointS2_2 = getPointByType("S2-2");
                    pointS3_1 = getPointByType("S3-1");
                    pointS3_2 = getPointByType("S3-2");
                    cA = pointA.getCoordinate().split(",");
                    valueList = cA[2] + "/" + AlertUtils.getLineLength(pointS1_1, pointS1_2) + "/"
                            + AlertUtils.getLineLength(pointS2_1, pointS2_2) + "/"
                            + AlertUtils.getLineLength(pointS3_1, pointS3_2);
                    break;
                default:
                    Log.d(LOG_TAG, "未知的开挖方法: 测点数目=" + pointCount);
                    break;
            }
            return valueList;
        }

        public void markAsUploaded() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao.defaultDao();
                    for (TunnelSettlementTotalData point : mMeasurePoints) {
                        point.setInfo("2");
                        dao.update(point);
                    }
                }
            }).start();
        }
    }

    public class UploadSubsidenceMeasureData {
        private List<SubsidenceTotalData> mMeasurePoints = new ArrayList<SubsidenceTotalData>();

        public void addPoint(SubsidenceTotalData point) {
            mMeasurePoints.add(point);
        }

        public List<SubsidenceTotalData> getPoints() {
            return mMeasurePoints;
        }

        public SubsidenceTotalData getPointByType(String type) {
            SubsidenceTotalData result = null;
            for (SubsidenceTotalData point : mMeasurePoints) {
                if (type.equals(point.getPntType())) {
                    result = point;
                }
            }
            return result;
        }

        public String getPointCodeList(String sectionCode) {
            String pointCodeList = "";
            final int pointCount = mMeasurePoints.size();
            switch (pointCount) {
                // 全断面法
                case 3:
                    pointCodeList = sectionCode + "GD01" + "/" + sectionCode + "SL01" + "#"
                            + sectionCode + "SL02";
                    break;
                    // 台阶法
                case 5:
                    pointCodeList = sectionCode + "GD01" + "/" + sectionCode + "SL01" + "#"
                            + sectionCode + "SL02" + "/" + sectionCode + "SL03" + "#" + sectionCode
                            + "SL04";
                    break;
                    // 三台阶法或双侧壁法
                case 7:
                    pointCodeList = sectionCode + "GD01" + "/" + sectionCode + "SL01" + "#"
                            + sectionCode + "SL02" + "/" + sectionCode + "SL03" + "#" + sectionCode
                            + "SL04" + "/" + sectionCode + "SL05" + "#" + sectionCode + "SL06";
                    break;
                default:
                    Log.d(LOG_TAG, "未知的开挖方法: 测点数目=" + pointCount);
                    break;
            }
            return pointCodeList;
        }

        public String getCoordinateList() {
            String coordinateList = "";
            final int pointCount = mMeasurePoints.size();
            SubsidenceTotalData pointA, pointS1_1, pointS1_2, pointS2_1, pointS2_2, pointS3_1, pointS3_2;
            String A, S1_1, S1_2, S2_1, S2_2, S3_1, S3_2, coordinate;
            switch (pointCount) {
                // 全断面法
                case 3:
                    pointA = getPointByType("A");
                    pointS1_1 = getPointByType("S1-1");
                    pointS1_2 = getPointByType("S1-2");
                    A = pointA.getCoordinate().replace(",", "#");
                    S1_1 = pointS1_1.getCoordinate().replace(",", "#");
                    S1_2 = pointS1_2.getCoordinate().replace(",", "#");
                    coordinate = A + "/" + S1_1 + "#" + S1_2;
                    coordinateList = RSACoder.encnryptDes(coordinate, Constant.testDeskey);
                    break;
                    // 台阶法
                case 5:
                    pointA = getPointByType("A");
                    pointS1_1 = getPointByType("S1-1");
                    pointS1_2 = getPointByType("S1-2");
                    pointS2_1 = getPointByType("S2-1");
                    pointS2_2 = getPointByType("S2-2");
                    A = pointA.getCoordinate().replace(",", "#");
                    S1_1 = pointS1_1.getCoordinate().replace(",", "#");
                    S1_2 = pointS1_2.getCoordinate().replace(",", "#");
                    S2_1 = pointS2_1.getCoordinate().replace(",", "#");
                    S2_2 = pointS2_2.getCoordinate().replace(",", "#");
                    coordinate = A + "/" + S1_1 + "#" + S1_2 + "/" + S2_1 + "#" + S2_2;
                    coordinateList = RSACoder.encnryptDes(coordinate, Constant.testDeskey);
                    break;
                    // 三台阶法或双侧壁法
                case 7:
                    pointA = getPointByType("A");
                    pointS1_1 = getPointByType("S1-1");
                    pointS1_2 = getPointByType("S1-2");
                    pointS2_1 = getPointByType("S2-1");
                    pointS2_2 = getPointByType("S2-2");
                    pointS3_1 = getPointByType("S3-1");
                    pointS3_2 = getPointByType("S3-2");
                    A = pointA.getCoordinate().replace(",", "#");
                    S1_1 = pointS1_1.getCoordinate().replace(",", "#");
                    S1_2 = pointS1_2.getCoordinate().replace(",", "#");
                    S2_1 = pointS2_1.getCoordinate().replace(",", "#");
                    S2_2 = pointS2_2.getCoordinate().replace(",", "#");
                    S3_1 = pointS3_1.getCoordinate().replace(",", "#");
                    S3_2 = pointS3_2.getCoordinate().replace(",", "#");
                    coordinate = A + "/" + S1_1 + "#" + S1_2 + "/" + S2_1 + "#" + S2_2 + "/" + S3_1
                            + "#" + S3_2;
                    coordinateList = RSACoder.encnryptDes(coordinate, Constant.testDeskey);
                    break;
                default:
                    Log.d(LOG_TAG, "未知的开挖方法: 测点数目=" + pointCount);
                    break;
            }
            return coordinateList;
        }

        // public String getValueList() {
        // String valueList = "";
        // final int pointCount = mMeasurePoints.size();
        // SubsidenceTotalData pointA, pointS1_1, pointS1_2, pointS2_1,
        // pointS2_2, pointS3_1, pointS3_2;
        // String[] cA;
        // switch (pointCount) {
        // // 全断面法
        // case 3:
        // pointA = getPointByType("A");
        // pointS1_1 = getPointByType("S1-1");
        // pointS1_2 = getPointByType("S1-2");
        // cA = pointA.getCoordinate().split(",");
        // valueList = cA[2] + "/" + AlertUtils.getLineLength(pointS1_1,
        // pointS1_2);
        // break;
        // // 台阶法
        // case 5:
        // pointA = getPointByType("A");
        // pointS1_1 = getPointByType("S1-1");
        // pointS1_2 = getPointByType("S1-2");
        // pointS2_1 = getPointByType("S2-1");
        // pointS2_2 = getPointByType("S2-2");
        // cA = pointA.getCoordinate().split(",");
        // valueList = cA[2] + "/" + AlertUtils.getLineLength(pointS1_1,
        // pointS1_2) + "/"
        // + AlertUtils.getLineLength(pointS2_1, pointS2_2);
        // break;
        // // 三台阶法或双侧壁法
        // case 7:
        // pointA = getPointByType("A");
        // pointS1_1 = getPointByType("S1-1");
        // pointS1_2 = getPointByType("S1-2");
        // pointS2_1 = getPointByType("S2-1");
        // pointS2_2 = getPointByType("S2-2");
        // pointS3_1 = getPointByType("S3-1");
        // pointS3_2 = getPointByType("S3-2");
        // cA = pointA.getCoordinate().split(",");
        // valueList = cA[2] + "/" + AlertUtils.getLineLength(pointS1_1,
        // pointS1_2) + "/"
        // + AlertUtils.getLineLength(pointS2_1, pointS2_2) + "/"
        // + AlertUtils.getLineLength(pointS3_1, pointS3_2);
        // break;
        // default:
        // Log.d(LOG_TAG, "未知的开挖方法: 测点数目=" + pointCount);
        // break;
        // }
        // return valueList;
        // }

        public void markAsUploaded() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SubsidenceTotalDataDao dao = SubsidenceTotalDataDao.defaultDao();
                    for (SubsidenceTotalData point : mMeasurePoints) {
                        point.setInfo("2");
                        dao.update(point);
                    }
                }
            }).start();
        }
    }
}
