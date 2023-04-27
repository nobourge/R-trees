package be.ulb.infof203.projet;

public class FileConst {
    public static final String PREFIX = "";

    // Découpe de la Belgique en secteurs statistiques
    public static final String STATBEL = "resources/sh_statbel_statistical_sectors_20210101/sh_statbel_statistical_sectors_20210101.shp";
        // Global bounds: ReferencedEnvelope[
        // 21991.632100000978 : 295167.0696000017,
        // 21162.160300001502 : 244027.21149999817]

        // iterative search in ms:
        // 306
        // 165
        // rtree search:

    // Pays du monde
    public static final String WB_COUNTRIES = "resources/WB_countries_Admin0_10m/WB_countries_Admin0_10m.shp";
    public static final String WB_BOUNDARY_LINES = "ressources/WB_Adm0_boundary_lines_10m.shp";
    public static final String WB_COAST_LINES = "resources/wb_coastlines_10m/WB_Coastlines_10m.shp";

    // Communes françaises
    public static final String REGIONS = "resources/regions-20180101-shp/regions-20180101.shp";

    // Indonesian government’s designation of legal forest area
    // https://data.globalforestwatch.org/datasets/kawasan-hutan-indonesia-indonesia-forest-area/about
    public static final String INDONESIAN_FOREST = "resources/kawasan_hutan_092017/kawasan_hutan_092017.shp";


}
