package onlyajar.airboat.utils;


import java.math.BigDecimal;
import java.math.RoundingMode;

public final class AmountUtils {
    private AmountUtils(){
        throw new UnsupportedOperationException();
    }
    public static String toCent(String dollar){
        BigDecimal bigDecimal = new BigDecimal(dollar);
        return bigDecimal.movePointRight(2).setScale(0, RoundingMode.HALF_UP).toPlainString();
    }

    public static String toCent(double dollar){
        BigDecimal bigDecimal = new BigDecimal(dollar);
        return bigDecimal.movePointRight(2).setScale(0, RoundingMode.HALF_UP).toPlainString();
    }

    public static String toCentPad12(String dollar){
        String cent = toCent(dollar);
        return PadUtils.zeroPadLeft(cent, 12);
    }

    public static String toCentPad12(double dollar){
        String cent = toCent(dollar);
        return PadUtils.zeroPadLeft(cent, 12);
    }


    public static String toDollar(long cent){
        BigDecimal bigDecimal = new BigDecimal(cent);
        return bigDecimal.movePointLeft(2).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public static String toDollar(String cent){
        BigDecimal bigDecimal = new BigDecimal(cent);
        return bigDecimal.movePointLeft(2).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
