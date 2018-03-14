import com.ce2tech.averager.model.dataacces.XlsReader;
import com.ce2tech.averager.model.dataacces.XlsWriter;
import com.ce2tech.averager.model.dataobjects.Measurand;
import com.ce2tech.averager.model.dataobjects.Measurement;
import com.ce2tech.averager.model.dataobjects.Sample;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@RunWith(DataProviderRunner.class)
public class XlsReaderTests {

    private Measurement testMeasurement = new Measurement();
    private XlsReader fileReader = new XlsReader();
    private XlsWriter fileWriter = new XlsWriter();

    @Before
    public void addTestDataToTestMeasurement() {
        Sample testSample = new Sample();
        testSample.add(new Measurand("Data", LocalDate.now()));
        testSample.add(new Measurand("Czas", LocalTime.now()));
        testSample.add(new Measurand("NO[ppm]", "-" ));
        testSample.add(new Measurand("SO2[ppm]", 2.0 ));

        testMeasurement.add(testSample);
        testMeasurement.add(testSample);
        testMeasurement.add(testSample);
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
        Measurement measurement;

        //When
        measurement = fileReader.readMeasurementFromFile(testFilePath);

        //Then
        assertThat(measurement.size()).isEqualTo(testFileMeasurementSize);
        for (List<Measurand> sample : measurement.getMeasurement())
            assertThat(sample.size()).isEqualTo(testFileSamplesSize);
    }

    @Test
    public void shouldCreateComponentRowInWorkbook() {
        //Given
        Workbook testWorkbook = fileWriter.prepareEmptyWorkbook();
        Sheet testSheet = testWorkbook.getSheetAt(0);

        //When
        fileWriter.writeComponentsRowToNextRowOfWorkbook(testMeasurement);

        //Then
        assertThat(testSheet.getPhysicalNumberOfRows()).isEqualTo(1);
        for (Row row : testSheet)
            assertThat(row.getPhysicalNumberOfCells()).isEqualTo(4);
    }

    @Test
    public void shouldCreateMultipleHeadersInWorkbook() {
        //Given
        Workbook testWorkbook = fileWriter.prepareEmptyWorkbook();
        Sheet testSheet = testWorkbook.getSheetAt(0);

        //When
        fileWriter.writeComponentsRowToNextRowOfWorkbook(testMeasurement);
        fileWriter.writeComponentsRowToNextRowOfWorkbook(testMeasurement);

        //Then
        assertThat(testSheet.getPhysicalNumberOfRows()).isEqualTo(2);
        for (Row row : testSheet)
            assertThat(row.getPhysicalNumberOfCells()).isEqualTo(4);
    }

    @Test
    public void shouldCreateMeasurementInWorkbook() {
        //Given
        Workbook testWorkbook = fileWriter.prepareEmptyWorkbook();
        Sheet testSheet = testWorkbook.getSheetAt(0);

        //When
        fileWriter.writeMeasurementToWorkbook(testMeasurement);

        //Then
        assertThat(testSheet.getPhysicalNumberOfRows()).isEqualTo(3);
        for (Row row : testSheet)
            assertThat(row.getPhysicalNumberOfCells()).isEqualTo(4);
    }

    @Test
    public void shouldCreateMultipleMeasurementsInWorkbook() {
        //Given
        Workbook testWorkbook = fileWriter.prepareEmptyWorkbook();
        Sheet testSheet = testWorkbook.getSheetAt(0);

        //When
        fileWriter.writeMeasurementToWorkbook(testMeasurement);
        fileWriter.writeMeasurementToWorkbook(testMeasurement);

        //Then
        assertThat(testSheet.getPhysicalNumberOfRows()).isEqualTo(6);
        for (Row row : testSheet)
            assertThat(row.getPhysicalNumberOfCells()).isEqualTo(4);
    }

}