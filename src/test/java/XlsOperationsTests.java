import com.ce2tech.averager.model.dao.XlsOpertions;
import com.ce2tech.averager.model.dto.Measurand;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@RunWith(DataProviderRunner.class)
public class XlsOperationsTests {

    private List< List<Measurand> > testMeasurement;

    @Before
    public void addTestDataToTestMeasurement() {
        List<Measurand> testSample = new ArrayList<>();
        testSample.add(new Measurand("Data", LocalDate.now()));
        testSample.add(new Measurand("Czas", LocalTime.now()));
        testSample.add(new Measurand("NO[ppm]", "-" ));
        testSample.add(new Measurand("SO2[ppm]", 2.0 ));

        List<Measurand> testSample2 = new ArrayList<>(testSample);
        List<Measurand> testSample3 = new ArrayList<>(testSample);

        testMeasurement = new ArrayList<>();
        testMeasurement.add(testSample);
        testMeasurement.add(testSample2);
        testMeasurement.add(testSample3);
    }

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
                {"testFile_randomWorkbook.xls", 0, 0},
                {"wrong-file-name.jpg", 0, 0},
                {"", 0, 0}
        };
    }

    @Test
    @UseDataProvider("fileSizeProvider")
    public void shouldReturnListWithDataFromFile(String testFilePath, int testFileSamplesSize, int testFileMeasurementSize) {
        //Given
        List< List<Measurand> > measurement;

        //When
        measurement = XlsOpertions.loadMeasurementFromFile(testFilePath);

        //Then
        assertThat(measurement.size()).isEqualTo(testFileMeasurementSize);
        for (List<Measurand> sample : measurement)
            assertThat(sample.size()).isEqualTo(testFileSamplesSize);
    }

    @Test
    public void shouldCreateHeaderInWorkbook() {
        //Given
        Workbook testWorkbook = new HSSFWorkbook();
        Sheet testSheet = testWorkbook.createSheet();

        //When
        XlsOpertions.createMeasurementHeaderInWorkbook(testWorkbook, testMeasurement);

        //Then
        assertThat(testSheet.getPhysicalNumberOfRows()).isEqualTo(1);
        for (Row row : testSheet)
            assertThat(row.getPhysicalNumberOfCells()).isEqualTo(4);
    }

    @Test
    public void shouldNotCreateHeaderInWorkbookWithoutSheets() {
        //Given
        Workbook testWorkbook = new HSSFWorkbook();

        //When
        XlsOpertions.createMeasurementHeaderInWorkbook(testWorkbook, testMeasurement);

        //Then
        assertThat(testWorkbook.getNumberOfSheets()).isEqualTo(0);
    }

    @Test
    public void shouldCreateMultipleHeadersInWorkbook() {
        //Given
        Workbook testWorkbook = new HSSFWorkbook();
        Sheet testSheet = testWorkbook.createSheet();

        //When
        XlsOpertions.createMeasurementHeaderInWorkbook(testWorkbook, testMeasurement);
        XlsOpertions.createMeasurementHeaderInWorkbook(testWorkbook, testMeasurement);

        //Then
        assertThat(testSheet.getPhysicalNumberOfRows()).isEqualTo(2);
        for (Row row : testSheet)
            assertThat(row.getPhysicalNumberOfCells()).isEqualTo(4);
    }

    @Test
    public void shouldCreateMeasurementInWorkbook() {
        //Given
        Workbook testWorkbook = new HSSFWorkbook();
        Sheet testSheet = testWorkbook.createSheet();

        //When
        XlsOpertions.createMeasurementInWorkbook(testWorkbook, testMeasurement);

        //Then
        assertThat(testSheet.getPhysicalNumberOfRows()).isEqualTo(3);
        for (Row row : testSheet)
            assertThat(row.getPhysicalNumberOfCells()).isEqualTo(4);
    }

    @Test
    public void shouldNotCreateMeasurementInWorkbookWithoutSheets() {
        //Given
        Workbook testWorkbook = new HSSFWorkbook();

        //When
        XlsOpertions.createMeasurementInWorkbook(testWorkbook, testMeasurement);

        //Then
        assertThat(testWorkbook.getNumberOfSheets()).isEqualTo(0);
    }

    @Test
    public void shouldCreateMultipleMeasurementsInWorkbook() {
        //Given
        Workbook testWorkbook = new HSSFWorkbook();
        Sheet testSheet = testWorkbook.createSheet();

        //When
        XlsOpertions.createMeasurementInWorkbook(testWorkbook, testMeasurement);
        XlsOpertions.createMeasurementInWorkbook(testWorkbook, testMeasurement);

        //Then
        assertThat(testSheet.getPhysicalNumberOfRows()).isEqualTo(6);
        for (Row row : testSheet)
            assertThat(row.getPhysicalNumberOfCells()).isEqualTo(4);
    }

}