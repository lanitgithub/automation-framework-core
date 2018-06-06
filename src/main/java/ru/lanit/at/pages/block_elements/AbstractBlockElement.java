package ru.lanit.at.pages.block_elements;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.support.PageFactory;
import ru.lanit.at.context.Context;
import ru.lanit.at.make.Make;
import ru.lanit.at.make.Wait;
import ru.lanit.at.pages.AbstractPage;
import ru.lanit.at.pages.PageCatalog;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

public class AbstractBlockElement extends HtmlElement {

    protected Logger log = LogManager.getLogger(getClass());
    protected Wait wait;
    protected Make make;

    private PageCatalog pageCatalog;

    public AbstractBlockElement() {
        wait = Context.getInstance().getBean(Wait.class);
        make = Context.getInstance().getBean(Make.class);
        pageCatalog = Context.getInstance().getBean(PageCatalog.class);
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

    /**
     * Initializes elements of block at the moment of invocation.
     */
    public void refreshElement() {
        log.warn("Refreshing element {}", this.getClass().getSimpleName());
        wait.sec(1);
        PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(make.getDriver())), this);
    }
}
