package resource.img;

import java.io.File;
import java.net.URL;
import application.model.node.pane.PaneUtils;
import consts.PartyConsts;
import dto.chara.enums.JOBManage.GRAVE;
import dto.chara.enums.JOBManage.JOB;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import resource.img.FullSizeAnimeManager.CharaCreationInfo;
import resource.img.enums.EVENT_TYPE;
import resource.img.utils.PlayUtil;
import resource.img.utils.ScaleUtil;

public class PaneAnimationManager {

    private static PaneAnimationManager instance = new PaneAnimationManager();

    public static PaneAnimationManager getInstance() {
        return instance;
    }

    // 戦闘用と
    private static ImageAnimationView[] _AnytimeChara;
    private static ImageAnimationView[] _BattleAnimation;
    private static ImageAnimationView[] _Grave;

    static {
        _AnytimeChara = new ImageAnimationView[PartyConsts._LimitOfMember];
        _BattleAnimation = new ImageAnimationView[PartyConsts._LimitOfMember];
        _Grave = new ImageAnimationView[PartyConsts._LimitOfMember];
    }

    public static void removeAnimationChara(int elm) {
        _AnytimeChara[elm] = null;
        _BattleAnimation[elm] = null;
    }

    public static void convertGrave(Pane[] panes, int elm) {
        System.out.println("PaneAnimationManager.convertGrave コンバート[" + elm + "]");
        _BattleAnimation[elm] = _Grave[elm];
        clearChildren(panes[elm]);
        playIfExist(EVENT_TYPE.BATTLE, panes[elm], elm);
    }

    public static boolean isGrave(int elm) {
        ImageAnimationView img = _BattleAnimation[elm];
        ImageAnimationView grave = _Grave[elm];
        return img == grave;
    }

    private static void clearChildren(Pane p) {
        p.getChildren().clear();
    }

    public static void playAllAnimationChara(EVENT_TYPE event, Pane... pn) {
        for (Pane p : pn) {
            playIfExist(event, p, PaneUtils.getId(p));
        }
    }

    private static void playIfExist(EVENT_TYPE event, Pane p, int elm) {
        ImageAnimationView[] imgArr = getArr(event);
        ImageAnimationView img = imgArr[elm];
        if (img == null) {
            return;
        }
        ScaleUtil.resetScales(img);
        // 毎回addしないと上手く動かない
        p.getChildren().add(img);
        img.play();
    }

    private static ImageAnimationView[] getArr(EVENT_TYPE event) {
        switch (event) {
            case ANYTIME:
                return _AnytimeChara;
            case BATTLE:
                return _BattleAnimation;
        }
        throw new IllegalArgumentException("入力値不正 EVENT[" + event + "]");
    }

    public void setAnimationChara(int playerNum) {
        CharaCreationInfo info = FullSizeAnimeManager.getCreationInfo();
        _AnytimeChara[playerNum] = makeImageBy(info.getJob());
        _BattleAnimation[playerNum] = makeImageBy(info.getJob());
        _Grave[playerNum] = makeSetGrave();
    }

    public static void copyToBattle() {
        int cnt = 0;
        for (ImageAnimationView imageAnimationView : _AnytimeChara) {
            if (imageAnimationView == null) {
                cnt++;
                continue;
            }
            _BattleAnimation[cnt] = imageAnimationView.deepCopy();
            cnt++;
        }
        for (ImageAnimationView battleImg : _BattleAnimation) {
            if (battleImg == null) {
                continue;
            }
            PlayUtil.playInfinityLoop(battleImg);
        }
        System.out.println("_BattleAnimationにコピーしました。[" + cnt + "]件");
    }

    private ImageAnimationView makeImageBy(JOB job) {
        URL uri = ClassLoader.getSystemResource(job.getMiniSizePath());
        File file = new File(uri.getFile());

        ImageAnimationView img = new ImageAnimationView(Duration.millis(800), file, 120, 120);
        PlayUtil.playInfinityLoop(img);

        return img;
    }

    private ImageAnimationView makeSetGrave() {
        URL uri = ClassLoader.getSystemResource(GRAVE.GRAVE_IMG.getMiniSizePath());
        File file = new File(uri.getFile());

        ImageAnimationView img = new ImageAnimationView(Duration.millis(800), file, 120, 120);
        PlayUtil.playInfinityLoop(img);

        return img;
    }

    public static ImageAnimationView loadNumber(int num) {

        String png = num + ".png";

        // TODO : ../でパスを指定する
        File file = new File("C:\\workspace\\3StepRPG\\number\\" + png);

        ImageAnimationView img = new ImageAnimationView(Duration.millis(600), file, 14, 40);
        img.setAnimationRange(1, img.getMaxIndex());

        return img;
        // return NUMBER.getImg(num);
    }
}
