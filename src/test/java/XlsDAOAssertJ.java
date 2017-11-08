import com.ce2tech.averager.model.TransferObject;
import com.ce2tech.averager.model.XlsDAO;

import static com.ce2tech.averager.myutils.TestingUtil.invokePrivateMethod;
import static org.assertj.core.api.Assertions.*;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(DataProviderRunner.class)
public class XlsDAOAssertJ {

    private XlsDAO dao;

    @DataProvider
    public static Object[][] fileSizeProvider() {
        return new Object[][]{
                //{file_path, file_row_length, file_column_length}
                {"testfile_tensecounds_no_temp.xls", 9, 739},
                {"testfile_tensecounds_no.xls", 8, 447},
                {"testfile_tensecounds.xls", 7, 364},
                {"testfile_oneminute.xls", 7, 62},
                {"wrong-file-name.jpg", 0, 0},
                {"", 0, 0}
        };
    }

    @Test
    @UseDataProvider("fileSizeProvider")
    public void shouldLoadHeaderFromFile(String testFilePath, int testFileRowSize, int testFileColumnSize) {
        //Given
        List<String> dataHeader;
        dao = new XlsDAO(testFilePath);

        //When
        dataHeader = (List<String>) invokePrivateMethod("loadHeaderFromFile", dao);

        //Then
        assertThat(dataHeader.size()).isEqualTo(testFileRowSize);
    }


    @Test
    @UseDataProvider("fileSizeProvider")
    public void shouldLoadDataFromFile(String testFilePath, int testFileRowSize, int testFileColumnSize) {
        //Given
        List<List<Object>> data;
        dao = new XlsDAO(testFilePath);

        //When
        data = (List<List<Object>>) invokePrivateMethod("loadDataFromFile", dao);

        //Then
        assertThat(data.size()).isEqualTo(testFileRowSize);

        for (List<Object> column : data) {
            assertThat(column.size()).isEqualTo(testFileColumnSize-1);  //-1 because of header
        }
    }

    @Test
    @UseDataProvider("fileSizeProvider")
    public void shouldCreateTransferObjectWithDataFromFile(String testFilePath, int testFileRowSize, int testFileColumnSize) {
        //Given
        TransferObject dto;
        dao = new XlsDAO(testFilePath);

        //When
        dto = dao.getData();

        //Then
        assertThat(dto.getDataHeader().size()).isEqualTo(testFileRowSize);
        assertThat(dto.getDataColumns().size()).isEqualTo(testFileRowSize);
        for (List<Object> column : dto.getDataColumns()) {
            assertThat(column.size()).isEqualTo(testFileColumnSize-1);  //-1 because of header
        }
    }

    @Test
    @UseDataProvider("fileSizeProvider")
    public void shouldCreateNewCopyOfFile(String testFilePath, int testFileRowSize, int testFileColumnSize) {
        //Given
        TransferObject copiedData;
        dao = new XlsDAO(testFilePath);

        //When
        dao.setData(dao.getData(), "newFile.xls");
        copiedData = ( new XlsDAO("newFile.xls") ).getData();

        //Then
        assertThat(copiedData.getDataHeader().size()).isEqualTo(testFileRowSize);
        assertThat(copiedData.getDataColumns().size()).isEqualTo(testFileRowSize);
        for (List<Object> column : copiedData.getDataColumns()) {
            assertThat(column.size()).isEqualTo(testFileColumnSize-1);  //-1 because of header
        }
    }

}