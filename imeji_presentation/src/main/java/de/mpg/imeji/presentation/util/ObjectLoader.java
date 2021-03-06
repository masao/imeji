/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.util;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.j2j.exceptions.NotFoundException;

public class ObjectLoader
{
    private static Logger logger = Logger.getLogger(ObjectLoader.class);

    public static CollectionImeji loadCollection(URI id, User user)
    {
        try
        {
            CollectionController cl = new CollectionController(user);
            return cl.retrieve(id);
        }
        catch (NotFoundException e)
        {
            writeErrorNotFound("collection", id);
        }
        catch (Exception e)
        {
            writeException(e, id.toString());
        }
        return null;
    }

    public static CollectionImeji loadCollectionLazy(URI id, User user)
    {
        try
        {
            CollectionController cl = new CollectionController(user);
            return cl.retrieveLazy(id);
        }
        catch (NotFoundException e)
        {
            writeErrorNotFound("collection", id);
        }
        catch (Exception e)
        {
            if (id != null)
                writeException(e, id.toString());
            else
                writeException(e, null);
        }
        return null;
    }

    public static Album loadAlbum(URI id, User user)
    {
        try
        {
            Album a = loadAlbumLazy(id, user);
            ItemController ic = new ItemController(user);
            return (Album)ic.loadContainerItems(a, user, -1, 0);
        }
        catch (Exception e)
        {
            writeException(e, id.toString());
        }
        return null;
    }

    public static Album loadAlbumLazy(URI id, User user)
    {
        try
        {
            AlbumController ac = new AlbumController(user);
            return ac.retrieveLazy(id, user);
        }
        catch (NotFoundException e)
        {
            writeErrorNotFound("album", id);
        }
        catch (Exception e)
        {
            writeException(e, id.toString());
        }
        return null;
    }

    public static Item loadImage(URI id, User user)
    {
        try
        {
            ItemController ic = new ItemController(user);
            return ic.retrieve(id);
        }
        catch (NotFoundException e)
        {
            writeErrorNotFound("image", id);
        }
        catch (Exception e)
        {
            writeException(e, id.toString());
        }
        return null;
    }

    public static User loadUser(String email, User user)
    {
        try
        {
            UserController uc = new UserController(user);
            return uc.retrieve(email);
        }
        catch (NotFoundException e)
        {
            writeErrorNotFound("user", URI.create(email));
        }
        catch (Exception e)
        {
            writeException(e, email);
        }
        return null;
    }

    public static MetadataProfile loadProfile(URI id, User user)
    {
        try
        {
            ProfileController pc = new ProfileController(user);
            MetadataProfile p = pc.retrieve(id);
            Collections.sort((List<Statement>)p.getStatements());
            return p;
        }
        catch (NotFoundException e)
        {
            writeErrorNotFound("profile", id);
        }
        catch (Exception e)
        {
            writeException(e, id.toString());
        }
        return null;
    }

    private static void writeErrorNotFound(String objectType, URI id)
    {
        BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel(objectType) + " " + id
                + " " + ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("not_found"));
        logger.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel(objectType) + " " + id + " "
                + ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("not_found"));
    }

    private static void writeException(Exception e, String id)
    {
        logger.error("Error Object loader for " + id, e);
    }
}
