import com.ce2tech.averager.model.XlsDAO;
import static org.assertj.core.api.Assertions.*;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(DataProviderRunner.class)
public class XlsDAOAssertJ {

    private XlsDAO dao;

    @DataProvider
    public static Object[][] fileSizeProvider() {
        return new Object[][] {
                //{file_patch, file_row_length, file_column_length}
                {"testfile_tensecounds_no_temp.xls", 9, 739},
                {"testfile_tensecounds.xls", 7, 364},
                {"testfile_oneminute_no.xls", 8, 447},
                {"testfile_oneminute.xls", 7, 62}
        };
    }

    //private String testFilePath = "testfile_tensecounds_no_temp.xls";
    //private int testFileRowSize = 9;
    //private int testFileColumnSize = 739;

    @Test
    @UseDataProvider("fileSizeProvider")
    public void shouldLoadHeaderFromFile(String testFilePath, int testFileRowSize, int testFileColumnSize) {
        //Given
        dao = new XlsDAO(testFilePath);

        //When
        dao.loadHeaderFromFile();

        //Then
        assertThat(dao.getDataHeader().size()).isEqualTo(testFileRowSize);
    }

    @Test
    @UseDataProvider("fileSizeProvider")
    public void shouldLoadDataFromFile(String testFilePath, int testFileRowSize, int testFileColumnSize) {
        //Given
        dao = new XlsDAO(testFilePath);

        //When
        dao.loadDataFromFile();

        //Then
        assertThat(dao.getData().size()).isEqualTo(testFileColumnSize-1);

        for (List<Object> row : dao.getData()) {
            assertThat(row.size()).isEqualTo(testFileRowSize);
        }
    }

}
