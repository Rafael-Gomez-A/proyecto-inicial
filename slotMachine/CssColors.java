import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Clase de utilidad que conoce los nombres estándar de colores CSS y puede
 * validar un nombre de color y convertirlo en un objeto {@link java.awt.Color}.
 *
 * Esta clase amplía el proyecto original "shapes": el componente Canvas
 * original solo reconocía 7 colores definidos internamente ("red", "yellow",
 * "blue", "green", "magenta", "black", "white"). SlotMachine requiere
 * símbolos identificados por cualquier nombre de color estándar de CSS;
 * por ello, esta tabla incorpora el resto de los colores CSS con nombre
 * y es utilizada tanto por SlotMachine (para validar la entrada del usuario)
 * como por la versión ampliada de {@link Canvas} (para dibujarlos).
 *
 *@author Juan Camilo Rojas / Rafael Ricardo Gomez
 *@version 1.0 (Ciclo 1)
 */
public class CssColors{

    private static final Map<String, Color> COLORS = new HashMap<String, Color>();

    static{
        put("aliceblue", 0xF0F8FF);   put("antiquewhite", 0xFAEBD7);
        put("aqua", 0x00FFFF);        put("aquamarine", 0x7FFFD4);
        put("azure", 0xF0FFFF);       put("beige", 0xF5F5DC);
        put("bisque", 0xFFE4C4);      put("black", 0x000000);
        put("blanchedalmond", 0xFFEBCD); put("blue", 0x0000FF);
        put("blueviolet", 0x8A2BE2);  put("brown", 0xA52A2A);
        put("burlywood", 0xDEB887);   put("cadetblue", 0x5F9EA0);
        put("chartreuse", 0x7FFF00);  put("chocolate", 0xD2691E);
        put("coral", 0xFF7F50);       put("cornflowerblue", 0x6495ED);
        put("cornsilk", 0xFFF8DC);    put("crimson", 0xDC143C);
        put("cyan", 0x00FFFF);        put("darkblue", 0x00008B);
        put("darkcyan", 0x008B8B);    put("darkgoldenrod", 0xB8860B);
        put("darkgray", 0xA9A9A9);    put("darkgreen", 0x006400);
        put("darkgrey", 0xA9A9A9);    put("darkkhaki", 0xBDB76B);
        put("darkmagenta", 0x8B008B); put("darkolivegreen", 0x556B2F);
        put("darkorange", 0xFF8C00);  put("darkorchid", 0x9932CC);
        put("darkred", 0x8B0000);     put("darksalmon", 0xE9967A);
        put("darkseagreen", 0x8FBC8F); put("darkslateblue", 0x483D8B);
        put("darkslategray", 0x2F4F4F); put("darkslategrey", 0x2F4F4F);
        put("darkturquoise", 0x00CED1); put("darkviolet", 0x9400D3);
        put("deeppink", 0xFF1493);    put("deepskyblue", 0x00BFFF);
        put("dimgray", 0x696969);     put("dimgrey", 0x696969);
        put("dodgerblue", 0x1E90FF);  put("firebrick", 0xB22222);
        put("floralwhite", 0xFFFAF0); put("forestgreen", 0x228B22);
        put("fuchsia", 0xFF00FF);     put("gainsboro", 0xDCDCDC);
        put("ghostwhite", 0xF8F8FF);  put("gold", 0xFFD700);
        put("goldenrod", 0xDAA520);   put("gray", 0x808080);
        put("grey", 0x808080);        put("green", 0x008000);
        put("greenyellow", 0xADFF2F); put("honeydew", 0xF0FFF0);
        put("hotpink", 0xFF69B4);     put("indianred", 0xCD5C5C);
        put("indigo", 0x4B0082);      put("ivory", 0xFFFFF0);
        put("khaki", 0xF0E68C);       put("lavender", 0xE6E6FA);
        put("lavenderblush", 0xFFF0F5); put("lawngreen", 0x7CFC00);
        put("lemonchiffon", 0xFFFACD); put("lightblue", 0xADD8E6);
        put("lightcoral", 0xF08080);  put("lightcyan", 0xE0FFFF);
        put("lightgoldenrodyellow", 0xFAFAD2); put("lightgray", 0xD3D3D3);
        put("lightgreen", 0x90EE90);  put("lightgrey", 0xD3D3D3);
        put("lightpink", 0xFFB6C1);   put("lightsalmon", 0xFFA07A);
        put("lightseagreen", 0x20B2AA); put("lightskyblue", 0x87CEFA);
        put("lightslategray", 0x778899); put("lightslategrey", 0x778899);
        put("lightsteelblue", 0xB0C4DE); put("lightyellow", 0xFFFFE0);
        put("lime", 0x00FF00);        put("limegreen", 0x32CD32);
        put("linen", 0xFAF0E6);       put("magenta", 0xFF00FF);
        put("maroon", 0x800000);      put("mediumaquamarine", 0x66CDAA);
        put("mediumblue", 0x0000CD);  put("mediumorchid", 0xBA55D3);
        put("mediumpurple", 0x9370DB); put("mediumseagreen", 0x3CB371);
        put("mediumslateblue", 0x7B68EE); put("mediumspringgreen", 0x00FA9A);
        put("mediumturquoise", 0x48D1CC); put("mediumvioletred", 0xC71585);
        put("midnightblue", 0x191970); put("mintcream", 0xF5FFFA);
        put("mistyrose", 0xFFE4E1);   put("moccasin", 0xFFE4B5);
        put("navajowhite", 0xFFDEAD); put("navy", 0x000080);
        put("oldlace", 0xFDF5E6);     put("olive", 0x808000);
        put("olivedrab", 0x6B8E23);   put("orange", 0xFFA500);
        put("orangered", 0xFF4500);   put("orchid", 0xDA70D6);
        put("palegoldenrod", 0xEEE8AA); put("palegreen", 0x98FB98);
        put("paleturquoise", 0xAFEEEE); put("palevioletred", 0xDB7093);
        put("papayawhip", 0xFFEFD5);  put("peachpuff", 0xFFDAB9);
        put("peru", 0xCD853F);        put("pink", 0xFFC0CB);
        put("plum", 0xDDA0DD);        put("powderblue", 0xB0E0E6);
        put("purple", 0x800080);      put("rebeccapurple", 0x663399);
        put("red", 0xFF0000);         put("rosybrown", 0xBC8F8F);
        put("royalblue", 0x4169E1);   put("saddlebrown", 0x8B4513);
        put("salmon", 0xFA8072);      put("sandybrown", 0xF4A460);
        put("seagreen", 0x2E8B57);    put("seashell", 0xFFF5EE);
        put("sienna", 0xA0522D);      put("silver", 0xC0C0C0);
        put("skyblue", 0x87CEEB);     put("slateblue", 0x6A5ACD);
        put("slategray", 0x708090);   put("slategrey", 0x708090);
        put("snow", 0xFFFAFA);        put("springgreen", 0x00FF7F);
        put("steelblue", 0x4682B4);   put("tan", 0xD2B48C);
        put("teal", 0x008080);        put("thistle", 0xD8BFD8);
        put("tomato", 0xFF6347);      put("turquoise", 0x40E0D0);
        put("violet", 0xEE82EE);      put("wheat", 0xF5DEB3);
        put("white", 0xFFFFFF);       put("whitesmoke", 0xF5F5F5);
        put("yellow", 0xFFFF00);      put("yellowgreen", 0x9ACD32);
    }

    private CssColors(){
        // utility class: no instances
    }

    private static void put(String name, int rgb){
        COLORS.put(name, new Color(rgb));
    }

    /**
     * Indica si el nombre proporcionado es una palabra clave de color CSS reconocida.
     * La comparación no distingue entre mayúsculas y minúsculas; "null" nunca es válido.
     */
    public static boolean isValid(String name){
        if(name == null){
            return false;
        }
        return COLORS.containsKey(name.trim().toLowerCase());
    }

    /**
     * Convierte un nombre de color CSS válido en un objeto java.awt.Color.
     */
    public static Color toAwtColor(String name){
        if(name == null){
            return Color.black;
        }
        Color color = COLORS.get(name.trim().toLowerCase());
        return color == null ? Color.black : color;
    }

    // Extensiones Ciclo 3

    /**
     * Retorna los nombres de color CSS sin repetir tonos, ordenados alfabeticamente.
     * Varios nombres CSS comparten el mismo tono (por ejemplo "gray" y "grey"); de
     * cada grupo solo se incluye el primero, de modo que dos nombres de la lista
     * nunca se ven iguales en pantalla.
     */
    public static List<String> distinctNames(){
        List<String> sorted = new ArrayList<String>(COLORS.keySet());
        Collections.sort(sorted);
        Set<Integer> seenTones = new HashSet<Integer>();
        List<String> result = new ArrayList<String>();
        for(String name : sorted){
            if(seenTones.add(COLORS.get(name).getRGB())){
                result.add(name);
            }
        }
        return result;
    }
}