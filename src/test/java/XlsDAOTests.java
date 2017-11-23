import com.ce2tech.averager.model.dao.XlsDAO;
import com.ce2tech.averager.model.dto.Measurand;
import com.ce2tech.averager.model.dto.TransferObject;
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
                {"testfile_tensecounds_no_temp.xls", 9, 738},
                {"testfile_tensecounds_no.xls", 8, 446},
                {"testfile_tensecounds.xls", 7, 363},
                {"testfile_oneminute.xls", 7, 61},
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
        TransferObject dto;

        //When
        dto = dao.getData();

        //Then
        assertThat(dto.getMeasurement().size()).isEqualTo(testMeasurementSize);
        for (List<Measurand> sample : dto.getMeasurement()) {
            assertThat(sample.size()).isEqualTo(testSamplesSize);
        }

    }


    @Test
    @UseDataProvider("fileSizeProvider")
    public void shouldCreateCopyOfFile(String testFilePath, int testSamplesSize, int testMeasurementSize) {
        //Given
        dao = new XlsDAO(testFilePath);
        TransferObject copiedData;

        //When
        dao.setData(dao.getData(), "newFile.xls");
        copiedData = ( new XlsDAO("newFile.xls") ).getData();

        //Then
        assertThat(copiedData.getMeasurement().size()).isEqualTo(testMeasurementSize);
        for (List<Measurand> copiedSample : copiedData.getMeasurement()) {
            assertThat(copiedSample.size()).isEqualTo(testSamplesSize);
        }

    }

}
