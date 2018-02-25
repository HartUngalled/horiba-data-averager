import com.ce2tech.averager.model.dataacces.XlsDAO;
import com.ce2tech.averager.model.dataobjects.Measurand;
import com.ce2tech.averager.model.dataobjects.Measurement;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@RunWith(DataProviderRunner.class)
public class XlsDAOTests {

    XlsDAO dao;

    @DataProvider
    public static Object[][] fileSizeProvider() {
        return new Object[][]{
                //{file_path, file_samples_length, file_measurement_length_without_header}
                {"testFile_tenSeconds_nox_temp.xls", 9, 738},
                {"testFile_tenSeconds_nox.xls", 8, 446},
                {"testFile_tenSeconds.xls", 7, 363},
                {"testFile_oneMinute.xls", 7, 61},
                {"testFile_messedUp.xls", 5, 2435},
                //Wrong files
                {"some_random_workbook.xls", 0, 0},
                {"wrong-file-name.jpg", 0, 0},
                {"", 0, 0}
        };
    }

    @Test
    @UseDataProvider("fileSizeProvider")
    public void shouldCreateDtoWithDataFromFile(String testFilePath, int testSamplesSize, int testMeasurementSize) {
        //Given
        dao = new XlsDAO(testFilePath);
        Measurement dto;

        //When
        dto = dao.getData();

        //Then
        assertThat(dto.getMeasurement().size()).isEqualTo(testMeasurementSize);
        for (List<Measurand> sample : dto.getMeasurement())
            assertThat(sample.size()).isEqualTo(testSamplesSize);
    }


    @Test
    @UseDataProvider("fileSizeProvider")
    public void shouldCreateCopyOfFile(String testFilePath, int testSamplesSize, int testMeasurementSize) {
        //Given
        dao = new XlsDAO(testFilePath);
        Measurement copiedData;

        //When
        dao.setData(dao.getData(), "newFile.xls");
        copiedData = ( new XlsDAO("newFile.xls") ).getData();

        //Then
        assertThat(copiedData.getMeasurement().size()).isEqualTo(testMeasurementSize);
        for (List<Measurand> copiedSample : copiedData.getMeasurement())
            assertThat(copiedSample.size()).isEqualTo(testSamplesSize);
    }
}
