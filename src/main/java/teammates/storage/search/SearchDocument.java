package teammates.storage.search;

import teammates.logic.api.Logic;

import com.google.appengine.api.search.Document;

/**
 * The SearchDocument object that defines how we store {@link Document}
 */
public abstract class SearchDocument {
    
    protected Logic logic;
    
    public SearchDocument() {
        logic = new Logic();
    }
    
    public Document build() {
        prepareData();
        return toDocument();
    }
    
    protected abstract void prepareData();
    
    protected abstract Document toDocument();
}
