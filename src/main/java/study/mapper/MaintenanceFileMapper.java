package study.mapper;
import org.apache.ibatis.annotations.Mapper;
import study.model.MaintenanceFile;

@Mapper
public interface MaintenanceFileMapper {
    //レコードを1件追加
    int insertMaintenanceFile(MaintenanceFile maintenanceFile);
}
