import com.ce2tech.averager.model.TransferObject;
import com.ce2tech.averager.model.XlsDAO;
import static org.assertj.core.api.Assertions.*;

import com.ce2tech.averager.presenter.XlsPresenter;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@RunWith(DataProviderRunner.class)
public class XlsDAOAssertJ {

    private XlsDAO dao;

    private Object invokePrivateMethod(String methodName) {
        try {
            Method method = XlsDAO.class.getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method.invoke(dao);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @DataProvider
    public static Object[][] fileSizeProvider() {
        return new Object[][]{
                //{file_path, file_row_length, file_column_length}
                {"testfile_tensecounds_no_temp.xls", 9, 739},
                {"testfile_tensecounds_no.xls", 8, 447},
                {"testfile_tensecounds.xls", 7, 364},
                {"testfile_oneminute.xls", 7, 62}
        };
    }

    @Test
    @UseDataProvider("fileSizeProvider")
    public void shouldLoadHeaderFromFile(String testFilePath, int testFileRowSize, int testFileColumnSize) {
        //Given
        List<String> dataHeader;
        dao = new XlsDAO(testFilePath);

        //When
        dataHeader = (List<String>) invokePrivateMethod("loadHeaderFromFile");

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
        data = (List<List<Object>>) invokePrivateMethod("loadDataFromFile");

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
        assertThat(dto).isNotNull();

        assertThat(dto.getDataHeader().size()).isEqualTo(testFileRowSize);

        assertThat(dto.getDataColumns().size()).isEqualTo(testFileRowSize);

        for (List<Object> column : dto.getDataColumns()) {
            assertThat(column.size()).isEqualTo(testFileColumnSize-1);  //-1 because of header
        }
    }

}