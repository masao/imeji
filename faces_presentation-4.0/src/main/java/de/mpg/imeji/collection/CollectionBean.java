package de.mpg.imeji.collection;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.mdProfile.MdProfileBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.Organization;
import de.mpg.jena.vo.Person;
import de.mpg.jena.vo.User;

public abstract class CollectionBean
{
    public enum TabType
    {
        COLLECTION, PROFILE, HOME;
    }

    private TabType tab = TabType.HOME;
    private SessionBean sessionBean = null;
    private CollectionImeji collection = null;
    private String id = null;
    private int authorPosition;
    private int organizationPosition;
    private List<SelectItem> profilesMenu = new ArrayList<SelectItem>();
    private boolean selected;

    public CollectionBean(CollectionImeji coll)
    {
        this.collection = coll;
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    }

    public CollectionBean()
    {
        collection = new CollectionImeji();
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    }

    public boolean valid()
    {
        boolean valid = true;
        boolean hasAuthor = false;
        if (collection.getMetadata().getTitle() == null || "".equals(collection.getMetadata().getTitle()))
        {
            BeanHelper.error(sessionBean.getMessage("collection_create_error_title"));
            valid = false;
        }
        for (Person c : collection.getMetadata().getPersons())
        {
            boolean hasOrganization = false;
            if (!"".equals(c.getFamilyName()))
            {
                hasAuthor = true;
            }
            for (Organization o : c.getOrganizations())
            {
                if (!"".equals(o.getName()) || "".equals(c.getFamilyName()))
                {
                    hasOrganization = true;
                }
                if (hasOrganization && "".equals(c.getFamilyName()))
                {
                    BeanHelper.error(sessionBean.getMessage("collection_create_error_family_name"));
                    valid = false;
                }
            }
            if (!hasOrganization)
            {
                BeanHelper.error(sessionBean.getMessage("collection_create_error_organization"));
                valid = false;
            }
        }
        if (!hasAuthor)
        {
            BeanHelper.error(sessionBean.getMessage("collection_create_error_author"));
            valid = false;
        }
        return valid;
    }

    public String addAuthor()
    {
        LinkedList<Person> list = (LinkedList<Person>)collection.getMetadata().getPersons();
        list.add(authorPosition + 1, ImejiFactory.newPerson());
        return getNavigationString();
    }

    public String removeAuthor()
    {
        if (authorPosition > 0)
        {
            LinkedList<Person> list = (LinkedList<Person>)collection.getMetadata().getPersons();
            list.remove(authorPosition);
        }
        return getNavigationString();
    }

    public String addOrganization()
    {
        LinkedList<Person> persons = (LinkedList<Person>)collection.getMetadata().getPersons();
        LinkedList<Organization> orgs = (LinkedList<Organization>)persons.get(authorPosition).getOrganizations();
        orgs.add(organizationPosition + 1, ImejiFactory.newOrganization());
        return getNavigationString();
    }

    public String removeOrganization()
    {
        if (organizationPosition > 0)
        {
            LinkedList<Person> persons = (LinkedList<Person>)collection.getMetadata().getPersons();
            LinkedList<Organization> orgs = (LinkedList<Organization>)persons.get(authorPosition).getOrganizations();
            orgs.remove(organizationPosition);
        }
        return getNavigationString();
    }

    protected abstract String getNavigationString();

    public int getAuthorPosition()
    {
        return authorPosition;
    }

    public void setAuthorPosition(int pos)
    {
        this.authorPosition = pos;
    }

    /**
     * @return the collectionPosition
     */
    public int getOrganizationPosition()
    {
        return organizationPosition;
    }

    /**
     * @param collectionPosition the collectionPosition to set
     */
    public void setOrganizationPosition(int organizationPosition)
    {
        this.organizationPosition = organizationPosition;
    }

    /**
     * @return the tab
     */
    public TabType getTab()
    {
        if (UrlHelper.getParameterValue("tab") != null)
        {
            tab = TabType.valueOf(UrlHelper.getParameterValue("tab").toUpperCase());
        }
        return tab;
    }

    /**
     * @param tab the tab to set
     */
    public void setTab(TabType tab)
    {
        this.tab = tab;
    }

    /**
     * @return the collection
     */
    public CollectionImeji getCollection()
    {
        return collection;
    }

    /**
     * @param collection the collection to set
     */
    public void setCollection(CollectionImeji collection)
    {
        this.collection = collection;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }

    public List<SelectItem> getProfilesMenu()
    {
        return profilesMenu;
    }

    public void setProfilesMenu(List<SelectItem> profilesMenu)
    {
        this.profilesMenu = profilesMenu;
    }

    /**
     * @return the selected
     */
    public boolean isSelected()
    {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public int getSize()
    {
        return collection.getImages().size();
    }

    public boolean getIsOwner()
    {
        if (sessionBean.getUser() != null)
        {
            return collection.getProperties().getCreatedBy().equals(ObjectHelper.getURI(User.class, sessionBean.getUser().getEmail()));
        }
        else
            return false;
    }

    public String release() throws Exception
    {
        CollectionController cc = new CollectionController(sessionBean.getUser());
        cc.release(collection);
        return "pretty:";
    }
}
