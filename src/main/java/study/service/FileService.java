package study.service;
import java.util.List;
import org.springframework.stereotype.Service;

import study.dto.request.FileRequest;
import study.dto.request.InspectionFileRequest;
import study.dto.request.MaintenanceFileRequest;
import study.mapper.InspectionFileMapper;
import study.mapper.MaintenanceFileMapper;
import study.model.InspectionFile;
import study.model.MaintenanceFile;

@Service
public class FileService {
    private final InspectionFileMapper inspectionFileMapper;
    private final MaintenanceFileMapper maintenanceFileMapper;

    public FileService(InspectionFileMapper inspectionFileMapper, MaintenanceFileMapper maintenanceFileMapper) {
        this.inspectionFileMapper = inspectionFileMapper;
        this.maintenanceFileMapper = maintenanceFileMapper;
    }

    public int insertFile(FileRequest fileRequest) {
        int id = 0;
        // 正体を判定して、適切なMapperを呼び出す
        if (fileRequest instanceof InspectionFileRequest inspection) {
            InspectionFile file = new InspectionFile();
            file.setName(inspection.getName());
            file.setDescription(inspection.getDescription());
            id = inspectionFileMapper.insertInspectionFile(file);
        } else if (fileRequest instanceof MaintenanceFileRequest maintenance) {
            MaintenanceFile file = new MaintenanceFile();
            file.setName(maintenance.getName());
            file.setDescription(maintenance.getDescription());
            id = maintenanceFileMapper.insertMaintenanceFile(file);
        }

        return id;
    }
}



