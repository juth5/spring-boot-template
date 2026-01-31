package study.mapper;
import org.apache.ibatis.annotations.Mapper;
import study.model.InspectionFile;

@Mapper
public interface InspectionFileMapper {
    //レコードを1件追加
    int insertInspectionFile(InspectionFile inspectionFile);
}
