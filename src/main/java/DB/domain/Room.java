package DB.domain;

public class Room {
    private int id;
    private String name;
    private String master_id;
    private String game_category;
    private int capacity;
    private int current_count = 0; //현재 방에 있는 인원수

    public Room(int id, String name, String master_id, String game_category) {
        this.id = id;
        this.name = name;
        this.master_id = master_id;
        this.game_category = game_category;
    }

    public String getMasterId(String userId) {
        return master_id;
    }
}
