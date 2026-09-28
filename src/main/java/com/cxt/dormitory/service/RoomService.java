package com.cxt.dormitory.service;

import com.cxt.dormitory.dto.BedInfo;
import com.cxt.dormitory.dto.RoomVO;
import com.cxt.dormitory.entity.Building;
import com.cxt.dormitory.entity.Room;
import com.cxt.dormitory.entity.RoomType;
import com.cxt.dormitory.entity.StayRecord;
import com.cxt.dormitory.entity.Student;
import com.cxt.dormitory.repository.BuildingRepository;
import com.cxt.dormitory.repository.RoomRepository;
import com.cxt.dormitory.repository.RoomTypeRepository;
import com.cxt.dormitory.repository.StayRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomService {

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private StayRecordRepository stayRecordRepository;

    @Autowired
    private com.cxt.dormitory.repository.StudentRepository studentRepository;

    // ---------- 楼栋 ----------

    public List<Building> findAllBuildings() {
        return buildingRepository.findAllByOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public Building findBuildingById(Long id) {
        return id == null ? null : buildingRepository.findById(id).orElse(null);
    }

    public void saveBuilding(Building building) {
        if (building.getName() == null || building.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("楼栋名称不能为空");
        }
        building.setName(building.getName().trim());
        buildingRepository.save(building);
    }

    @Transactional
    public void deleteBuilding(Long id) {
        if (roomRepository.findByBuildingIdWithDetails(id).size() > 0) {
            throw new IllegalStateException("该楼栋下还有房间，不能删除");
        }
        buildingRepository.deleteById(id);
    }

    // ---------- 房型 ----------

    public List<RoomType> findAllRoomTypes() {
        return roomTypeRepository.findAllByOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public RoomType findRoomTypeById(Long id) {
        return id == null ? null : roomTypeRepository.findById(id).orElse(null);
    }

    public void saveRoomType(RoomType roomType) {
        if (roomType.getName() == null || roomType.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("房型名称不能为空");
        }
        if (roomType.getBedCount() == null || roomType.getBedCount() < 1) {
            throw new IllegalArgumentException("床位数必须大于0");
        }
        roomType.setName(roomType.getName().trim());
        roomTypeRepository.save(roomType);
    }

    @Transactional
    public void deleteRoomType(Long id) {
        List<Long> buildingIds = new ArrayList<Long>();
        List<Building> buildings = buildingRepository.findAllByOrderByIdAsc();
        for (Building b : buildings) {
            buildingIds.add(b.getId());
        }
        for (Long buildingId : buildingIds) {
            for (Room room : roomRepository.findByBuildingIdWithDetails(buildingId)) {
                if (room.getRoomType() != null && room.getRoomType().getId().equals(id)) {
                    throw new IllegalStateException("该房型已被房间使用，不能删除");
                }
            }
        }
        roomTypeRepository.deleteById(id);
    }

    // ---------- 房间 ----------

    public List<Room> findRoomsByBuilding(Long buildingId) {
        return roomRepository.findByBuildingIdWithDetails(buildingId);
    }

    /** 房间列表（带已入住床位数） */
    public List<RoomVO> findRoomVOsByBuilding(Long buildingId) {
        List<RoomVO> result = new ArrayList<RoomVO>();
        if (buildingId == null) {
            return result;
        }
        List<Room> rooms = roomRepository.findByBuildingIdWithDetails(buildingId);
        for (Room room : rooms) {
            long occupied = stayRecordRepository.findActiveByRoomWithStudent(room.getId()).size();
            result.add(new RoomVO(room, occupied));
        }
        return result;
    }

    /** 全部学生（供分配床位选择） */
    public List<Student> listStudents() {
        return studentRepository.findAll();
    }

    /** 全部房间（供分配床位选择） */
    public List<Room> listAllRooms() {
        return roomRepository.findAllWithDetails();
    }

    @Transactional(readOnly = true)
    public Room findRoomById(Long id) {
        return id == null ? null : roomRepository.findByIdWithDetails(id);
    }

    public void saveRoom(Room room) {
        if (room.getBuilding() == null || room.getBuilding().getId() == null) {
            throw new IllegalArgumentException("请选择所属楼栋");
        }
        if (room.getRoomType() == null || room.getRoomType().getId() == null) {
            throw new IllegalArgumentException("请选择房型");
        }
        if (room.getRoomNo() == null || room.getRoomNo().trim().isEmpty()) {
            throw new IllegalArgumentException("房间号不能为空");
        }
        Long buildingId = room.getBuilding().getId();
        String roomNo = room.getRoomNo().trim();
        room.setRoomNo(roomNo);
        boolean duplicated = room.getId() == null
                ? roomRepository.existsByBuildingIdAndRoomNo(buildingId, roomNo)
                : roomRepository.existsByBuildingIdAndRoomNoAndIdNot(buildingId, roomNo, room.getId());
        if (duplicated) {
            throw new IllegalArgumentException("该房间号已存在：" + roomNo);
        }
        RoomType roomType = roomTypeRepository.findById(room.getRoomType().getId()).orElse(null);
        room.setRoomType(roomType);
        Building building = buildingRepository.findById(buildingId).orElse(null);
        room.setBuilding(building);
        roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        List<StayRecord> active = stayRecordRepository.findActiveByRoomWithStudent(id);
        if (active.size() > 0) {
            throw new IllegalStateException("该房间还有学生入住，不能删除");
        }
        List<StayRecord> all = stayRecordRepository.findByRoomIdPlain(id);
        stayRecordRepository.deleteAll(all);
        roomRepository.deleteById(id);
    }

    /**
     * 房间床位可视化：生成 bedCount 个床位的信息
     */
    @Transactional(readOnly = true)
    public List<BedInfo> buildBedInfos(Long roomId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        List<BedInfo> beds = new ArrayList<BedInfo>();
        if (room == null || room.getRoomType() == null) {
            return beds;
        }
        int bedCount = room.getRoomType().getBedCount() == null ? 4 : room.getRoomType().getBedCount();
        List<StayRecord> active = stayRecordRepository.findActiveByRoomWithStudent(roomId);
        for (int i = 1; i <= bedCount; i++) {
            String bedNo = i + "号床";
            BedInfo info = new BedInfo();
            info.setBedNo(bedNo);
            info.setOccupied(false);
            for (StayRecord stay : active) {
                if (bedNo.equals(stay.getBedNo())) {
                    info.setOccupied(true);
                    info.setStudentName(stay.getStudent().getName());
                    info.setStudentId(stay.getStudent().getId());
                    info.setStayId(stay.getId());
                    info.setCheckInDate(stay.getCheckInDate() == null ? "" : stay.getCheckInDate().toString());
                    break;
                }
            }
            beds.add(info);
        }
        return beds;
    }
}
