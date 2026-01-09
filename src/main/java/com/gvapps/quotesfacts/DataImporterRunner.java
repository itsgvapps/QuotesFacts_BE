package com.gvapps.quotesfacts;

import com.gvapps.quotesfacts.service.test.ArticlesImportService;
import com.gvapps.quotesfacts.service.test.FactTypeImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataImporterRunner implements CommandLineRunner {

    private final FactTypeImportService importService;
    private final ArticlesImportService articlesImportService;

    @Override
    public void run(String... args) {
//        importService.importFactTypesFromJson();
//        importService.importFactDetailsFromJson();
//        importService.importFactDetailsTempFromJson();//FACT_DETAILS_TEMP.json
//        articlesImportService.importArticlesFromJson();
//        articlesImportService.importInsertUpdateArticlesFromJson("temp/articles_life_501_600.json");
//        articlesImportService.printIdByArticleTagFromJson("temp/articles_life_501_600.json", "Lifestyle");
//        articlesImportService.updateSubTitleFromJson();
    }
}

//articles_life_501_600, articles_life_601_700, articles_life_701_800, articles_life_801_900, articles_life_901_1000
