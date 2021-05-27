package pers.tz.rabbit.task.enums;

/**
 * @Author twz
 * @Date 2021-05-26
 * @Desc TODO
 */
public enum ElasticJobTypeEnum {

    SIMPLE("SimpleJob", "简单类型Job"),
    DATAFLOW("DataFlowJob", "流式类型Job"),
    SCRIPT("ScriptJob", "脚本类型Job");

    private String type;
    private String desc;

    ElasticJobTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }}
