import static org.assertj.core.api.Assertions.*;

import com.ce2tech.averager.presenter.XlsPresenter;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RunWith(DataProviderRunner.class)
public class XlsPresenterAssertJ {

    private XlsPresenter presenter;

    private Object invokePrivateMethod(String methodName) {
        try {
            Method method = XlsPresenter.class.getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method.invoke(presenter);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @DataProvider
    public static String[] filePathProvider() {
        return new String[]
                {"testfile_tensecounds_no_temp.xls",
                        "testfile_tensecounds_no.xls",
                        "testfile_tensecounds.xls",
                        "testfile_oneminute.xls"};
    }

    @Test
    @UseDataProvider("filePathProvider")
    public void shouldReturnLocalTimeIndex(String testFilePath) {
        //Given
        presenter = new XlsPresenter(testFilePath);

        //When
        int localTimeIndex = (int) invokePrivateMethod("getFileLocalTimeIndex");

        //Then
        assertThat(localTimeIndex).isEqualTo(1);
    }

    @Test
    @UseDataProvider("filePathProvider")
    public void shouldReturnSamplingTime(String testFilePath) {
        //Given
        presenter = new XlsPresenter(testFilePath);

        //When
        int samplingTime = (int) invokePrivateMethod("getDataSamplingTime");

        //Then
        if ( testFilePath.contains("tensecounds") ) {
            assertThat(samplingTime).isEqualTo(10);
        } else {
            assertThat(samplingTime).isEqualTo(60);
        }
    }

    @Test
    public void shouldAverageData() {
        //Given
        presenter = new XlsPresenter("testfile_tensecounds_no.xls");

        //When
        presenter.averageToOneMin();

    }



}
