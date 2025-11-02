package study.mapper;
import org.apache.ibatis.annotations.Mapper;
import study.model.Embedding;

@Mapper
public interface ApiEmbeddingMapper {
    //レコードを1件追加
    void insertEmbedding(String content, double[] embedding);

    //検索したEmbeddingを返す
    Embedding searchSimilar(double[] queryVector, int limit);
}