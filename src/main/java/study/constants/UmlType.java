package study.constants;

public enum UmlType {
    // public static final UmlType FLOWCHART = new UmlType("flowchart", "..."); //イメージ
    FLOWCHART("flowchart", ""),
    SEQUENCE("sequenceDiagram", "1. 登場人物（要素）の定義は、すべて「participant」を使用してください。");

    private final String code;
    private final String caution;

    UmlType(String code, String caution) {
        this.code = code;
        this.caution = caution;
    }

    // type（文字列）から該当する注意文を探すメソッド
    public static String findCaution(String typeCode) {
        for (UmlType type : values()) {
            if (type.code.equals(typeCode)) {
                return type.caution;
            }
        }
        return "";
    }
}