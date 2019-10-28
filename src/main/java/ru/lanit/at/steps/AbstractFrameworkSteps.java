package ru.lanit.at.steps;

import io.qameta.atlas.core.Atlas;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.springframework.context.ApplicationContext;
import ru.lanit.at.assertion.AssertsManager;
import ru.lanit.at.context.Context;
import ru.lanit.at.driver.DriverManager;
import ru.lanit.at.exceptions.FrameworkRuntimeException;
import ru.lanit.at.pages.AbstractPage;
import ru.lanit.at.pages.PageCatalog;
import ru.lanit.at.pages.block.AbstractBlockElement;
import ru.lanit.at.pages.element.UIElement;

import java.util.Map;

public abstract class AbstractFrameworkSteps {

    protected Logger log = LogManager.getLogger(this.getClass());
    protected AssertsManager assertsManager;
    protected PageCatalog pageCatalog;
    private DriverManager driverManager;
    private Atlas atlas;


    public AbstractFrameworkSteps() {
        ApplicationContext context = Context.getInstance();
        pageCatalog = context.getBean(PageCatalog.class);
        assertsManager = context.getBean(AssertsManager.class);
        driverManager = context.getBean(DriverManager.class);
        atlas = pageCatalog.getAtlas();
    }

    /**
     * @return Instance of {@link WebDriver} which is active in current thread.
     */
    protected WebDriver getDriver() {
        return driverManager.getDriver();
    }

    protected void shutdownDriver() {
        driverManager.shutdown();
    }

    protected boolean driverIsActive() {
        return driverManager.isActive();
    }

    /**
     * Returns instance of page with given class from {@link PageCatalog}. If {@link PageCatalog} doesn't contain page with such page yet - it will be initialized and saved.
     *
     * @param clazz Class of page object that should be initialized and returned.
     * @return Instance of page object from {@link PageCatalog}.
     */
    protected <T extends AbstractPage> T getPage(Class<T> clazz) {
        return pageCatalog.getPage(clazz);
    }

    protected <T extends AbstractPage> T getPageByTitle(String title) {
        return pageCatalog.getPageByTitle(title);
    }

    protected AbstractPage getCurrentPage() {
        return pageCatalog.getCurrentPage();
    }

    protected void setCurrentBlock(Class<? extends AbstractBlockElement> clazz, String... params) {
        Object object = getSearchContext();
        if (object == null) {
            throw new FrameworkRuntimeException("Для установки блока, требуется установить текущую страницу," +
                    " используя методы 'getPage/getPageByTitle'");
        }
        AbstractBlockElement abstractBlockElement;
        if (!AbstractPage.class.isAssignableFrom(object.getClass())) {
            abstractBlockElement = ((AbstractBlockElement) object).getBlockElement(clazz, params);
        } else {
            abstractBlockElement = ((AbstractPage) object).getBlockElement(clazz, params);
        }


        pageCatalog.setCurrentBlock(abstractBlockElement);
    }

    protected void setCurrentBlockByName(String name, String... params) {
        Class<? extends AbstractBlockElement> abstractBlockElement = pageCatalog.findBlockByName(name);
        setCurrentBlock(abstractBlockElement, params);
    }

    protected AbstractBlockElement getCurrentBlock() {
        return pageCatalog.getCurrentBlockElement();
    }

    protected <T extends UIElement> T getUIElement(Class<? extends UIElement> clazz, String... params) {
        Object obj = getSearchContext();
        if (obj == null) {
            throw new FrameworkRuntimeException("Установите контекст поиска элемента на страницу/блок");
        }
        if (!AbstractPage.class.isAssignableFrom(obj.getClass())) {
            return (T) ((AbstractBlockElement) obj).getElement(clazz, params);
        }
        return (T) ((AbstractPage) obj).getElement(clazz, params);
    }

    protected <T extends UIElement> T getUIElementByName(String nameElement, String... params) {
        Class<? extends UIElement> clazz = pageCatalog.findUIElemByName(nameElement);
        return getUIElement(clazz, params);
    }


    public Object getSearchContext() {
        if (pageCatalog.getCurrentBlockElement() == null) {
            return pageCatalog.getCurrentPage();
        }
        return pageCatalog.getCurrentBlockElement();
    }

    public void resetCurrentBlock() {
        pageCatalog.setCurrentBlock(null);
    }


    /**
     * @return Data keeper object as map.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getDataKeeper() {
        return (Map<String, Object>) Context.getInstance().getBean("dataKeeper");
    }

    /**
     * Saves any test data which can be accessible during all test runtime by specified key.
     *
     * @param key   Unique key to save test data.
     * @param value Any data that should be saved.
     */
    protected void saveTestData(String key, Object value) {
//        if (value instanceof CharSequence || value instanceof Number) logToAllure("Saved text data", "Key: \"" + key + "\", value: \"" + value + "\"");
        log.info("Saved text data {}: {}", key, value);
        getDataKeeper().put(key, value);
    }

//    @Attachment(value = "{0}", type = "text/plain")
//    protected String logToAllure(String messageType, String message) {
//        log.info("{}: {}", messageType, message);
//        return message;
//    }

    /**
     * Returns saved test data by specified key. Automatically casts object into requested data type.
     *
     * @param key
     * @param <T> Automatically casts saved object to requested type.
     *            E.g. {@code Foo foo = getTestData("foo");}
     * @return Saved value by given key or {@code null} if there is no saved data with given key.
     * @throws ClassCastException In case when value by given key can't be cast into requested type.
     */
    @SuppressWarnings("unchecked")
    protected <T> T getTestData(String key) {
        return (T) getDataKeeper().get(key);
    }

    /**
     * Erases all saved test data.
     */
    protected void clearTestData() {
        getDataKeeper().clear();
    }


    /**
     * Method to retrieve and interact with instance of {@link Atlas}.
     *
     * @return {@link Atlas} bean.
     */
    public Atlas getAtlas() {
        return atlas;
    }

    protected PageCatalog getPageCatalog() {
        return pageCatalog;
    }
}
