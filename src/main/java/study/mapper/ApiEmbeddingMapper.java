package study.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import study.model.Embedding;

@Mapper
public interface ApiEmbeddingMapper {
    //レコードを1件追加
    void insertEmbedding(String content, double[] embedding);

    //検索したEmbeddingを返す
    List<Embedding> searchSimilar(double[] queryVector, int limit);
}