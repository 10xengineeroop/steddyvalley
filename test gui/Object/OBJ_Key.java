package Object;

import javax.imageio.ImageIO;

public class OBJ_Key extends SuperObject {

    public OBJ_Key() {
        name = "Key";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/asset/object/chest.png"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    

}
