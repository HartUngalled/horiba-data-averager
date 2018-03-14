import com.ce2tech.averager.model.dataacces.XlsDAO;
import com.ce2tech.averager.model.dataobjects.Measurand;
import com.ce2tech.averager.model.dataobjects.Measurement;
import com.ce2tech.averager.model.dataobjects.Sample;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@RunWith(DataProviderRunner.class)
public class XlsDAOTests {

    @DataProvider
    public static Object[][] fileSizeProvider() {
        return new Object[][]{
                //{file_path, file_samples_length, file_measurement_length_without_components_row}
                {"testFile_tenSeconds_nox_temp.xls", 9, 738},
                {"testFile_tenSeconds_nox.xls", 8, 446},
                {"testFile_tenSeconds.xls", 7, 363},
                {"testFile_oneMinute.xls", 7, 61},
                {"testFile_messedUp.xls", 5, 2435},
                {"some_random_workbook.xls", 0, 0},
                {"wrong-file-name.jpg", 0, 0},
                {"", 0, 0}
        };
    }

    @Test
    @UseDataProvider("fileSizeProvider")
    public void shouldCreateMeasurementFromFile(String testFilePath, int testSamplesSize, int testMeasurementSize) {
        //Given
        XlsDAO dao = new XlsDAO(testFilePath);

        //When
        Measurement measurement = dao.getData();

        //Then
        assertThat(measurement.size()).isEqualTo(testMeasurementSize);
        for (Sample sample : measurement)
            assertThat(sample.size()).isEqualTo(testSamplesSize);
    }


    @Test
    @UseDataProvider("fileSizeProvider")
    public void shouldCreateCopyOfFile(String testFilePath, int testSamplesSize, int testMeasurementSize) {
        //Given
        XlsDAO fileCopyDao = new XlsDAO("fileCopy.xls");
        XlsDAO dao = new XlsDAO(testFilePath);
        Measurement measurement = dao.getData();

        //When
        dao.setData(measurement, "fileCopy.xls");
        Measurement fileCopyMeasurement = fileCopyDao.getData();

        //Then
        assertThat(fileCopyMeasurement.size()).isEqualTo(testMeasurementSize);
        for (Sample fileCopySample : fileCopyMeasurement)
            assertThat(fileCopySample.size()).isEqualTo(testSamplesSize);
    }
}
