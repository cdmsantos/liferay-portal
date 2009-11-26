/**
 * Copyright (c) 2000-2009 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portlet.messageboards.service.persistence;

import com.liferay.portal.NoSuchModelException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.annotation.BeanReference;
import com.liferay.portal.kernel.cache.CacheRegistry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.ModelListener;
import com.liferay.portal.service.persistence.BatchSessionUtil;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;

import com.liferay.portlet.messageboards.NoSuchStatsUserException;
import com.liferay.portlet.messageboards.model.MBStatsUser;
import com.liferay.portlet.messageboards.model.impl.MBStatsUserImpl;
import com.liferay.portlet.messageboards.model.impl.MBStatsUserModelImpl;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <a href="MBStatsUserPersistenceImpl.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * @author    Brian Wing Shun Chan
 * @see       MBStatsUserPersistence
 * @see       MBStatsUserUtil
 * @generated
 */
public class MBStatsUserPersistenceImpl extends BasePersistenceImpl<MBStatsUser>
	implements MBStatsUserPersistence {
	public static final String FINDER_CLASS_NAME_ENTITY = MBStatsUserImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST = FINDER_CLASS_NAME_ENTITY +
		".List";
	public static final FinderPath FINDER_PATH_FIND_BY_GROUPID = new FinderPath(MBStatsUserModelImpl.ENTITY_CACHE_ENABLED,
			MBStatsUserModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByGroupId", new String[] { Long.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_BY_OBC_GROUPID = new FinderPath(MBStatsUserModelImpl.ENTITY_CACHE_ENABLED,
			MBStatsUserModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByGroupId",
			new String[] {
				Long.class.getName(),
				
			"java.lang.Integer", "java.lang.Integer",
				"com.liferay.portal.kernel.util.OrderByComparator"
			});
	public static final FinderPath FINDER_PATH_COUNT_BY_GROUPID = new FinderPath(MBStatsUserModelImpl.ENTITY_CACHE_ENABLED,
			MBStatsUserModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"countByGroupId", new String[] { Long.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_BY_USERID = new FinderPath(MBStatsUserModelImpl.ENTITY_CACHE_ENABLED,
			MBStatsUserModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByUserId", new String[] { Long.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_BY_OBC_USERID = new FinderPath(MBStatsUserModelImpl.ENTITY_CACHE_ENABLED,
			MBStatsUserModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByUserId",
			new String[] {
				Long.class.getName(),
				
			"java.lang.Integer", "java.lang.Integer",
				"com.liferay.portal.kernel.util.OrderByComparator"
			});
	public static final FinderPath FINDER_PATH_COUNT_BY_USERID = new FinderPath(MBStatsUserModelImpl.ENTITY_CACHE_ENABLED,
			MBStatsUserModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"countByUserId", new String[] { Long.class.getName() });
	public static final FinderPath FINDER_PATH_FETCH_BY_G_U = new FinderPath(MBStatsUserModelImpl.ENTITY_CACHE_ENABLED,
			MBStatsUserModelImpl.FINDER_CACHE_ENABLED,
			FINDER_CLASS_NAME_ENTITY, "fetchByG_U",
			new String[] { Long.class.getName(), Long.class.getName() });
	public static final FinderPath FINDER_PATH_COUNT_BY_G_U = new FinderPath(MBStatsUserModelImpl.ENTITY_CACHE_ENABLED,
			MBStatsUserModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"countByG_U",
			new String[] { Long.class.getName(), Long.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_BY_G_M = new FinderPath(MBStatsUserModelImpl.ENTITY_CACHE_ENABLED,
			MBStatsUserModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByG_M",
			new String[] { Long.class.getName(), Integer.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_BY_OBC_G_M = new FinderPath(MBStatsUserModelImpl.ENTITY_CACHE_ENABLED,
			MBStatsUserModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByG_M",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				
			"java.lang.Integer", "java.lang.Integer",
				"com.liferay.portal.kernel.util.OrderByComparator"
			});
	public static final FinderPath FINDER_PATH_COUNT_BY_G_M = new FinderPath(MBStatsUserModelImpl.ENTITY_CACHE_ENABLED,
			MBStatsUserModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"countByG_M",
			new String[] { Long.class.getName(), Integer.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_ALL = new FinderPath(MBStatsUserModelImpl.ENTITY_CACHE_ENABLED,
			MBStatsUserModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(MBStatsUserModelImpl.ENTITY_CACHE_ENABLED,
			MBStatsUserModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"countAll", new String[0]);

	public void cacheResult(MBStatsUser mbStatsUser) {
		EntityCacheUtil.putResult(MBStatsUserModelImpl.ENTITY_CACHE_ENABLED,
			MBStatsUserImpl.class, mbStatsUser.getPrimaryKey(), mbStatsUser);

		FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_G_U,
			new Object[] {
				new Long(mbStatsUser.getGroupId()),
				new Long(mbStatsUser.getUserId())
			}, mbStatsUser);
	}

	public void cacheResult(List<MBStatsUser> mbStatsUsers) {
		for (MBStatsUser mbStatsUser : mbStatsUsers) {
			if (EntityCacheUtil.getResult(
						MBStatsUserModelImpl.ENTITY_CACHE_ENABLED,
						MBStatsUserImpl.class, mbStatsUser.getPrimaryKey(), this) == null) {
				cacheResult(mbStatsUser);
			}
		}
	}

	public void clearCache() {
		CacheRegistry.clear(MBStatsUserImpl.class.getName());
		EntityCacheUtil.clearCache(MBStatsUserImpl.class.getName());
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST);
	}

	public MBStatsUser create(long statsUserId) {
		MBStatsUser mbStatsUser = new MBStatsUserImpl();

		mbStatsUser.setNew(true);
		mbStatsUser.setPrimaryKey(statsUserId);

		return mbStatsUser;
	}

	public MBStatsUser remove(Serializable primaryKey)
		throws NoSuchModelException, SystemException {
		return remove(((Long)primaryKey).longValue());
	}

	public MBStatsUser remove(long statsUserId)
		throws NoSuchStatsUserException, SystemException {
		Session session = null;

		try {
			session = openSession();

			MBStatsUser mbStatsUser = (MBStatsUser)session.get(MBStatsUserImpl.class,
					new Long(statsUserId));

			if (mbStatsUser == null) {
				if (_log.isWarnEnabled()) {
					_log.warn("No MBStatsUser exists with the primary key " +
						statsUserId);
				}

				throw new NoSuchStatsUserException(
					"No MBStatsUser exists with the primary key " +
					statsUserId);
			}

			return remove(mbStatsUser);
		}
		catch (NoSuchStatsUserException nsee) {
			throw nsee;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public MBStatsUser remove(MBStatsUser mbStatsUser)
		throws SystemException {
		for (ModelListener<MBStatsUser> listener : listeners) {
			listener.onBeforeRemove(mbStatsUser);
		}

		mbStatsUser = removeImpl(mbStatsUser);

		for (ModelListener<MBStatsUser> listener : listeners) {
			listener.onAfterRemove(mbStatsUser);
		}

		return mbStatsUser;
	}

	protected MBStatsUser removeImpl(MBStatsUser mbStatsUser)
		throws SystemException {
		mbStatsUser = toUnwrappedModel(mbStatsUser);

		Session session = null;

		try {
			session = openSession();

			if (mbStatsUser.isCachedModel() || BatchSessionUtil.isEnabled()) {
				Object staleObject = session.get(MBStatsUserImpl.class,
						mbStatsUser.getPrimaryKeyObj());

				if (staleObject != null) {
					session.evict(staleObject);
				}
			}

			session.delete(mbStatsUser);

			session.flush();
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST);

		MBStatsUserModelImpl mbStatsUserModelImpl = (MBStatsUserModelImpl)mbStatsUser;

		FinderCacheUtil.removeResult(FINDER_PATH_FETCH_BY_G_U,
			new Object[] {
				new Long(mbStatsUserModelImpl.getOriginalGroupId()),
				new Long(mbStatsUserModelImpl.getOriginalUserId())
			});

		EntityCacheUtil.removeResult(MBStatsUserModelImpl.ENTITY_CACHE_ENABLED,
			MBStatsUserImpl.class, mbStatsUser.getPrimaryKey());

		return mbStatsUser;
	}

	public MBStatsUser updateImpl(
		com.liferay.portlet.messageboards.model.MBStatsUser mbStatsUser,
		boolean merge) throws SystemException {
		mbStatsUser = toUnwrappedModel(mbStatsUser);

		boolean isNew = mbStatsUser.isNew();

		MBStatsUserModelImpl mbStatsUserModelImpl = (MBStatsUserModelImpl)mbStatsUser;

		Session session = null;

		try {
			session = openSession();

			BatchSessionUtil.update(session, mbStatsUser, merge);

			mbStatsUser.setNew(false);
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST);

		EntityCacheUtil.putResult(MBStatsUserModelImpl.ENTITY_CACHE_ENABLED,
			MBStatsUserImpl.class, mbStatsUser.getPrimaryKey(), mbStatsUser);

		if (!isNew &&
				((mbStatsUser.getGroupId() != mbStatsUserModelImpl.getOriginalGroupId()) ||
				(mbStatsUser.getUserId() != mbStatsUserModelImpl.getOriginalUserId()))) {
			FinderCacheUtil.removeResult(FINDER_PATH_FETCH_BY_G_U,
				new Object[] {
					new Long(mbStatsUserModelImpl.getOriginalGroupId()),
					new Long(mbStatsUserModelImpl.getOriginalUserId())
				});
		}

		if (isNew ||
				((mbStatsUser.getGroupId() != mbStatsUserModelImpl.getOriginalGroupId()) ||
				(mbStatsUser.getUserId() != mbStatsUserModelImpl.getOriginalUserId()))) {
			FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_G_U,
				new Object[] {
					new Long(mbStatsUser.getGroupId()),
					new Long(mbStatsUser.getUserId())
				}, mbStatsUser);
		}

		return mbStatsUser;
	}

	protected MBStatsUser toUnwrappedModel(MBStatsUser mbStatsUser) {
		if (mbStatsUser instanceof MBStatsUserImpl) {
			return mbStatsUser;
		}

		MBStatsUserImpl mbStatsUserImpl = new MBStatsUserImpl();

		mbStatsUserImpl.setNew(mbStatsUser.isNew());
		mbStatsUserImpl.setPrimaryKey(mbStatsUser.getPrimaryKey());

		mbStatsUserImpl.setStatsUserId(mbStatsUser.getStatsUserId());
		mbStatsUserImpl.setGroupId(mbStatsUser.getGroupId());
		mbStatsUserImpl.setUserId(mbStatsUser.getUserId());
		mbStatsUserImpl.setMessageCount(mbStatsUser.getMessageCount());
		mbStatsUserImpl.setLastPostDate(mbStatsUser.getLastPostDate());

		return mbStatsUserImpl;
	}

	public MBStatsUser findByPrimaryKey(Serializable primaryKey)
		throws NoSuchModelException, SystemException {
		return findByPrimaryKey(((Long)primaryKey).longValue());
	}

	public MBStatsUser findByPrimaryKey(long statsUserId)
		throws NoSuchStatsUserException, SystemException {
		MBStatsUser mbStatsUser = fetchByPrimaryKey(statsUserId);

		if (mbStatsUser == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("No MBStatsUser exists with the primary key " +
					statsUserId);
			}

			throw new NoSuchStatsUserException(
				"No MBStatsUser exists with the primary key " + statsUserId);
		}

		return mbStatsUser;
	}

	public MBStatsUser fetchByPrimaryKey(Serializable primaryKey)
		throws SystemException {
		return fetchByPrimaryKey(((Long)primaryKey).longValue());
	}

	public MBStatsUser fetchByPrimaryKey(long statsUserId)
		throws SystemException {
		MBStatsUser mbStatsUser = (MBStatsUser)EntityCacheUtil.getResult(MBStatsUserModelImpl.ENTITY_CACHE_ENABLED,
				MBStatsUserImpl.class, statsUserId, this);

		if (mbStatsUser == null) {
			Session session = null;

			try {
				session = openSession();

				mbStatsUser = (MBStatsUser)session.get(MBStatsUserImpl.class,
						new Long(statsUserId));
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (mbStatsUser != null) {
					cacheResult(mbStatsUser);
				}

				closeSession(session);
			}
		}

		return mbStatsUser;
	}

	public List<MBStatsUser> findByGroupId(long groupId)
		throws SystemException {
		Object[] finderArgs = new Object[] { new Long(groupId) };

		List<MBStatsUser> list = (List<MBStatsUser>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_GROUPID,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler();

				query.append(_SQL_SELECT_MBSTATSUSER_WHERE);

				query.append("mbStatsUser.groupId = ?");

				query.append(" ORDER BY ");

				query.append("mbStatsUser.messageCount DESC");

				Query q = session.createQuery(query.toString());

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(groupId);

				list = q.list();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<MBStatsUser>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_GROUPID,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public List<MBStatsUser> findByGroupId(long groupId, int start, int end)
		throws SystemException {
		return findByGroupId(groupId, start, end, null);
	}

	public List<MBStatsUser> findByGroupId(long groupId, int start, int end,
		OrderByComparator obc) throws SystemException {
		Object[] finderArgs = new Object[] {
				new Long(groupId),
				
				String.valueOf(start), String.valueOf(end), String.valueOf(obc)
			};

		List<MBStatsUser> list = (List<MBStatsUser>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_OBC_GROUPID,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler();

				query.append(_SQL_SELECT_MBSTATSUSER_WHERE);

				query.append("mbStatsUser.groupId = ?");

				if (obc != null) {
					query.append(" ORDER BY ");

					String[] orderByFields = obc.getOrderByFields();

					for (int i = 0; i < orderByFields.length; i++) {
						query.append("mbStatsUser.");
						query.append(orderByFields[i]);

						if (obc.isAscending()) {
							query.append(" ASC");
						}
						else {
							query.append(" DESC");
						}

						if ((i + 1) < orderByFields.length) {
							query.append(", ");
						}
					}
				}

				else {
					query.append(" ORDER BY ");

					query.append("mbStatsUser.messageCount DESC");
				}

				Query q = session.createQuery(query.toString());

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(groupId);

				list = (List<MBStatsUser>)QueryUtil.list(q, getDialect(),
						start, end);
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<MBStatsUser>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_OBC_GROUPID,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public MBStatsUser findByGroupId_First(long groupId, OrderByComparator obc)
		throws NoSuchStatsUserException, SystemException {
		List<MBStatsUser> list = findByGroupId(groupId, 0, 1, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler();

			msg.append("No MBStatsUser exists with the key {");

			msg.append("groupId=" + groupId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchStatsUserException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public MBStatsUser findByGroupId_Last(long groupId, OrderByComparator obc)
		throws NoSuchStatsUserException, SystemException {
		int count = countByGroupId(groupId);

		List<MBStatsUser> list = findByGroupId(groupId, count - 1, count, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler();

			msg.append("No MBStatsUser exists with the key {");

			msg.append("groupId=" + groupId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchStatsUserException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public MBStatsUser[] findByGroupId_PrevAndNext(long statsUserId,
		long groupId, OrderByComparator obc)
		throws NoSuchStatsUserException, SystemException {
		MBStatsUser mbStatsUser = findByPrimaryKey(statsUserId);

		int count = countByGroupId(groupId);

		Session session = null;

		try {
			session = openSession();

			StringBundler query = new StringBundler();

			query.append(_SQL_SELECT_MBSTATSUSER_WHERE);

			query.append("mbStatsUser.groupId = ?");

			if (obc != null) {
				query.append(" ORDER BY ");

				String[] orderByFields = obc.getOrderByFields();

				for (int i = 0; i < orderByFields.length; i++) {
					query.append("mbStatsUser.");
					query.append(orderByFields[i]);

					if (obc.isAscending()) {
						query.append(" ASC");
					}
					else {
						query.append(" DESC");
					}

					if ((i + 1) < orderByFields.length) {
						query.append(", ");
					}
				}
			}

			else {
				query.append(" ORDER BY ");

				query.append("mbStatsUser.messageCount DESC");
			}

			Query q = session.createQuery(query.toString());

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);

			Object[] objArray = QueryUtil.getPrevAndNext(q, count, obc,
					mbStatsUser);

			MBStatsUser[] array = new MBStatsUserImpl[3];

			array[0] = (MBStatsUser)objArray[0];
			array[1] = (MBStatsUser)objArray[1];
			array[2] = (MBStatsUser)objArray[2];

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<MBStatsUser> findByUserId(long userId)
		throws SystemException {
		Object[] finderArgs = new Object[] { new Long(userId) };

		List<MBStatsUser> list = (List<MBStatsUser>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_USERID,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler();

				query.append(_SQL_SELECT_MBSTATSUSER_WHERE);

				query.append("mbStatsUser.userId = ?");

				query.append(" ORDER BY ");

				query.append("mbStatsUser.messageCount DESC");

				Query q = session.createQuery(query.toString());

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(userId);

				list = q.list();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<MBStatsUser>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_USERID,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public List<MBStatsUser> findByUserId(long userId, int start, int end)
		throws SystemException {
		return findByUserId(userId, start, end, null);
	}

	public List<MBStatsUser> findByUserId(long userId, int start, int end,
		OrderByComparator obc) throws SystemException {
		Object[] finderArgs = new Object[] {
				new Long(userId),
				
				String.valueOf(start), String.valueOf(end), String.valueOf(obc)
			};

		List<MBStatsUser> list = (List<MBStatsUser>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_OBC_USERID,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler();

				query.append(_SQL_SELECT_MBSTATSUSER_WHERE);

				query.append("mbStatsUser.userId = ?");

				if (obc != null) {
					query.append(" ORDER BY ");

					String[] orderByFields = obc.getOrderByFields();

					for (int i = 0; i < orderByFields.length; i++) {
						query.append("mbStatsUser.");
						query.append(orderByFields[i]);

						if (obc.isAscending()) {
							query.append(" ASC");
						}
						else {
							query.append(" DESC");
						}

						if ((i + 1) < orderByFields.length) {
							query.append(", ");
						}
					}
				}

				else {
					query.append(" ORDER BY ");

					query.append("mbStatsUser.messageCount DESC");
				}

				Query q = session.createQuery(query.toString());

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(userId);

				list = (List<MBStatsUser>)QueryUtil.list(q, getDialect(),
						start, end);
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<MBStatsUser>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_OBC_USERID,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public MBStatsUser findByUserId_First(long userId, OrderByComparator obc)
		throws NoSuchStatsUserException, SystemException {
		List<MBStatsUser> list = findByUserId(userId, 0, 1, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler();

			msg.append("No MBStatsUser exists with the key {");

			msg.append("userId=" + userId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchStatsUserException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public MBStatsUser findByUserId_Last(long userId, OrderByComparator obc)
		throws NoSuchStatsUserException, SystemException {
		int count = countByUserId(userId);

		List<MBStatsUser> list = findByUserId(userId, count - 1, count, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler();

			msg.append("No MBStatsUser exists with the key {");

			msg.append("userId=" + userId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchStatsUserException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public MBStatsUser[] findByUserId_PrevAndNext(long statsUserId,
		long userId, OrderByComparator obc)
		throws NoSuchStatsUserException, SystemException {
		MBStatsUser mbStatsUser = findByPrimaryKey(statsUserId);

		int count = countByUserId(userId);

		Session session = null;

		try {
			session = openSession();

			StringBundler query = new StringBundler();

			query.append(_SQL_SELECT_MBSTATSUSER_WHERE);

			query.append("mbStatsUser.userId = ?");

			if (obc != null) {
				query.append(" ORDER BY ");

				String[] orderByFields = obc.getOrderByFields();

				for (int i = 0; i < orderByFields.length; i++) {
					query.append("mbStatsUser.");
					query.append(orderByFields[i]);

					if (obc.isAscending()) {
						query.append(" ASC");
					}
					else {
						query.append(" DESC");
					}

					if ((i + 1) < orderByFields.length) {
						query.append(", ");
					}
				}
			}

			else {
				query.append(" ORDER BY ");

				query.append("mbStatsUser.messageCount DESC");
			}

			Query q = session.createQuery(query.toString());

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(userId);

			Object[] objArray = QueryUtil.getPrevAndNext(q, count, obc,
					mbStatsUser);

			MBStatsUser[] array = new MBStatsUserImpl[3];

			array[0] = (MBStatsUser)objArray[0];
			array[1] = (MBStatsUser)objArray[1];
			array[2] = (MBStatsUser)objArray[2];

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public MBStatsUser findByG_U(long groupId, long userId)
		throws NoSuchStatsUserException, SystemException {
		MBStatsUser mbStatsUser = fetchByG_U(groupId, userId);

		if (mbStatsUser == null) {
			StringBundler msg = new StringBundler();

			msg.append("No MBStatsUser exists with the key {");

			msg.append("groupId=" + groupId);

			msg.append(", ");
			msg.append("userId=" + userId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			if (_log.isWarnEnabled()) {
				_log.warn(msg.toString());
			}

			throw new NoSuchStatsUserException(msg.toString());
		}

		return mbStatsUser;
	}

	public MBStatsUser fetchByG_U(long groupId, long userId)
		throws SystemException {
		return fetchByG_U(groupId, userId, true);
	}

	public MBStatsUser fetchByG_U(long groupId, long userId,
		boolean retrieveFromCache) throws SystemException {
		Object[] finderArgs = new Object[] { new Long(groupId), new Long(userId) };

		Object result = null;

		if (retrieveFromCache) {
			result = FinderCacheUtil.getResult(FINDER_PATH_FETCH_BY_G_U,
					finderArgs, this);
		}

		if (result == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler();

				query.append(_SQL_SELECT_MBSTATSUSER_WHERE);

				query.append("mbStatsUser.groupId = ?");

				query.append(" AND ");

				query.append("mbStatsUser.userId = ?");

				query.append(" ORDER BY ");

				query.append("mbStatsUser.messageCount DESC");

				Query q = session.createQuery(query.toString());

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(groupId);

				qPos.add(userId);

				List<MBStatsUser> list = q.list();

				result = list;

				MBStatsUser mbStatsUser = null;

				if (list.isEmpty()) {
					FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_G_U,
						finderArgs, list);
				}
				else {
					mbStatsUser = list.get(0);

					cacheResult(mbStatsUser);

					if ((mbStatsUser.getGroupId() != groupId) ||
							(mbStatsUser.getUserId() != userId)) {
						FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_G_U,
							finderArgs, mbStatsUser);
					}
				}

				return mbStatsUser;
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (result == null) {
					FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_G_U,
						finderArgs, new ArrayList<MBStatsUser>());
				}

				closeSession(session);
			}
		}
		else {
			if (result instanceof List<?>) {
				return null;
			}
			else {
				return (MBStatsUser)result;
			}
		}
	}

	public List<MBStatsUser> findByG_M(long groupId, int messageCount)
		throws SystemException {
		Object[] finderArgs = new Object[] {
				new Long(groupId), new Integer(messageCount)
			};

		List<MBStatsUser> list = (List<MBStatsUser>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_G_M,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler();

				query.append(_SQL_SELECT_MBSTATSUSER_WHERE);

				query.append("mbStatsUser.groupId = ?");

				query.append(" AND ");

				query.append("mbStatsUser.messageCount != ?");

				query.append(" ORDER BY ");

				query.append("mbStatsUser.messageCount DESC");

				Query q = session.createQuery(query.toString());

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(groupId);

				qPos.add(messageCount);

				list = q.list();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<MBStatsUser>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_G_M, finderArgs,
					list);

				closeSession(session);
			}
		}

		return list;
	}

	public List<MBStatsUser> findByG_M(long groupId, int messageCount,
		int start, int end) throws SystemException {
		return findByG_M(groupId, messageCount, start, end, null);
	}

	public List<MBStatsUser> findByG_M(long groupId, int messageCount,
		int start, int end, OrderByComparator obc) throws SystemException {
		Object[] finderArgs = new Object[] {
				new Long(groupId), new Integer(messageCount),
				
				String.valueOf(start), String.valueOf(end), String.valueOf(obc)
			};

		List<MBStatsUser> list = (List<MBStatsUser>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_OBC_G_M,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler();

				query.append(_SQL_SELECT_MBSTATSUSER_WHERE);

				query.append("mbStatsUser.groupId = ?");

				query.append(" AND ");

				query.append("mbStatsUser.messageCount != ?");

				if (obc != null) {
					query.append(" ORDER BY ");

					String[] orderByFields = obc.getOrderByFields();

					for (int i = 0; i < orderByFields.length; i++) {
						query.append("mbStatsUser.");
						query.append(orderByFields[i]);

						if (obc.isAscending()) {
							query.append(" ASC");
						}
						else {
							query.append(" DESC");
						}

						if ((i + 1) < orderByFields.length) {
							query.append(", ");
						}
					}
				}

				else {
					query.append(" ORDER BY ");

					query.append("mbStatsUser.messageCount DESC");
				}

				Query q = session.createQuery(query.toString());

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(groupId);

				qPos.add(messageCount);

				list = (List<MBStatsUser>)QueryUtil.list(q, getDialect(),
						start, end);
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<MBStatsUser>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_OBC_G_M,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public MBStatsUser findByG_M_First(long groupId, int messageCount,
		OrderByComparator obc) throws NoSuchStatsUserException, SystemException {
		List<MBStatsUser> list = findByG_M(groupId, messageCount, 0, 1, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler();

			msg.append("No MBStatsUser exists with the key {");

			msg.append("groupId=" + groupId);

			msg.append(", ");
			msg.append("messageCount=" + messageCount);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchStatsUserException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public MBStatsUser findByG_M_Last(long groupId, int messageCount,
		OrderByComparator obc) throws NoSuchStatsUserException, SystemException {
		int count = countByG_M(groupId, messageCount);

		List<MBStatsUser> list = findByG_M(groupId, messageCount, count - 1,
				count, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler();

			msg.append("No MBStatsUser exists with the key {");

			msg.append("groupId=" + groupId);

			msg.append(", ");
			msg.append("messageCount=" + messageCount);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchStatsUserException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public MBStatsUser[] findByG_M_PrevAndNext(long statsUserId, long groupId,
		int messageCount, OrderByComparator obc)
		throws NoSuchStatsUserException, SystemException {
		MBStatsUser mbStatsUser = findByPrimaryKey(statsUserId);

		int count = countByG_M(groupId, messageCount);

		Session session = null;

		try {
			session = openSession();

			StringBundler query = new StringBundler();

			query.append(_SQL_SELECT_MBSTATSUSER_WHERE);

			query.append("mbStatsUser.groupId = ?");

			query.append(" AND ");

			query.append("mbStatsUser.messageCount != ?");

			if (obc != null) {
				query.append(" ORDER BY ");

				String[] orderByFields = obc.getOrderByFields();

				for (int i = 0; i < orderByFields.length; i++) {
					query.append("mbStatsUser.");
					query.append(orderByFields[i]);

					if (obc.isAscending()) {
						query.append(" ASC");
					}
					else {
						query.append(" DESC");
					}

					if ((i + 1) < orderByFields.length) {
						query.append(", ");
					}
				}
			}

			else {
				query.append(" ORDER BY ");

				query.append("mbStatsUser.messageCount DESC");
			}

			Query q = session.createQuery(query.toString());

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);

			qPos.add(messageCount);

			Object[] objArray = QueryUtil.getPrevAndNext(q, count, obc,
					mbStatsUser);

			MBStatsUser[] array = new MBStatsUserImpl[3];

			array[0] = (MBStatsUser)objArray[0];
			array[1] = (MBStatsUser)objArray[1];
			array[2] = (MBStatsUser)objArray[2];

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<Object> findWithDynamicQuery(DynamicQuery dynamicQuery)
		throws SystemException {
		Session session = null;

		try {
			session = openSession();

			dynamicQuery.compile(session);

			return dynamicQuery.list();
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<Object> findWithDynamicQuery(DynamicQuery dynamicQuery,
		int start, int end) throws SystemException {
		Session session = null;

		try {
			session = openSession();

			dynamicQuery.setLimit(start, end);

			dynamicQuery.compile(session);

			return dynamicQuery.list();
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<MBStatsUser> findAll() throws SystemException {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	public List<MBStatsUser> findAll(int start, int end)
		throws SystemException {
		return findAll(start, end, null);
	}

	public List<MBStatsUser> findAll(int start, int end, OrderByComparator obc)
		throws SystemException {
		Object[] finderArgs = new Object[] {
				String.valueOf(start), String.valueOf(end), String.valueOf(obc)
			};

		List<MBStatsUser> list = (List<MBStatsUser>)FinderCacheUtil.getResult(FINDER_PATH_FIND_ALL,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler();

				query.append(_SQL_SELECT_MBSTATSUSER);

				if (obc != null) {
					query.append(" ORDER BY ");

					String[] orderByFields = obc.getOrderByFields();

					for (int i = 0; i < orderByFields.length; i++) {
						query.append("mbStatsUser.");
						query.append(orderByFields[i]);

						if (obc.isAscending()) {
							query.append(" ASC");
						}
						else {
							query.append(" DESC");
						}

						if ((i + 1) < orderByFields.length) {
							query.append(", ");
						}
					}
				}

				else {
					query.append(" ORDER BY ");

					query.append("mbStatsUser.messageCount DESC");
				}

				Query q = session.createQuery(query.toString());

				if (obc == null) {
					list = (List<MBStatsUser>)QueryUtil.list(q, getDialect(),
							start, end, false);

					Collections.sort(list);
				}
				else {
					list = (List<MBStatsUser>)QueryUtil.list(q, getDialect(),
							start, end);
				}
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<MBStatsUser>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_ALL, finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public void removeByGroupId(long groupId) throws SystemException {
		for (MBStatsUser mbStatsUser : findByGroupId(groupId)) {
			remove(mbStatsUser);
		}
	}

	public void removeByUserId(long userId) throws SystemException {
		for (MBStatsUser mbStatsUser : findByUserId(userId)) {
			remove(mbStatsUser);
		}
	}

	public void removeByG_U(long groupId, long userId)
		throws NoSuchStatsUserException, SystemException {
		MBStatsUser mbStatsUser = findByG_U(groupId, userId);

		remove(mbStatsUser);
	}

	public void removeByG_M(long groupId, int messageCount)
		throws SystemException {
		for (MBStatsUser mbStatsUser : findByG_M(groupId, messageCount)) {
			remove(mbStatsUser);
		}
	}

	public void removeAll() throws SystemException {
		for (MBStatsUser mbStatsUser : findAll()) {
			remove(mbStatsUser);
		}
	}

	public int countByGroupId(long groupId) throws SystemException {
		Object[] finderArgs = new Object[] { new Long(groupId) };

		Long count = (Long)FinderCacheUtil.getResult(FINDER_PATH_COUNT_BY_GROUPID,
				finderArgs, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler();

				query.append(_SQL_COUNT_MBSTATSUSER_WHERE);

				query.append("mbStatsUser.groupId = ?");

				Query q = session.createQuery(query.toString());

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(groupId);

				count = (Long)q.uniqueResult();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (count == null) {
					count = Long.valueOf(0);
				}

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_GROUPID,
					finderArgs, count);

				closeSession(session);
			}
		}

		return count.intValue();
	}

	public int countByUserId(long userId) throws SystemException {
		Object[] finderArgs = new Object[] { new Long(userId) };

		Long count = (Long)FinderCacheUtil.getResult(FINDER_PATH_COUNT_BY_USERID,
				finderArgs, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler();

				query.append(_SQL_COUNT_MBSTATSUSER_WHERE);

				query.append("mbStatsUser.userId = ?");

				Query q = session.createQuery(query.toString());

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(userId);

				count = (Long)q.uniqueResult();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (count == null) {
					count = Long.valueOf(0);
				}

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_USERID,
					finderArgs, count);

				closeSession(session);
			}
		}

		return count.intValue();
	}

	public int countByG_U(long groupId, long userId) throws SystemException {
		Object[] finderArgs = new Object[] { new Long(groupId), new Long(userId) };

		Long count = (Long)FinderCacheUtil.getResult(FINDER_PATH_COUNT_BY_G_U,
				finderArgs, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler();

				query.append(_SQL_COUNT_MBSTATSUSER_WHERE);

				query.append("mbStatsUser.groupId = ?");

				query.append(" AND ");

				query.append("mbStatsUser.userId = ?");

				Query q = session.createQuery(query.toString());

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(groupId);

				qPos.add(userId);

				count = (Long)q.uniqueResult();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (count == null) {
					count = Long.valueOf(0);
				}

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_G_U, finderArgs,
					count);

				closeSession(session);
			}
		}

		return count.intValue();
	}

	public int countByG_M(long groupId, int messageCount)
		throws SystemException {
		Object[] finderArgs = new Object[] {
				new Long(groupId), new Integer(messageCount)
			};

		Long count = (Long)FinderCacheUtil.getResult(FINDER_PATH_COUNT_BY_G_M,
				finderArgs, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler();

				query.append(_SQL_COUNT_MBSTATSUSER_WHERE);

				query.append("mbStatsUser.groupId = ?");

				query.append(" AND ");

				query.append("mbStatsUser.messageCount != ?");

				Query q = session.createQuery(query.toString());

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(groupId);

				qPos.add(messageCount);

				count = (Long)q.uniqueResult();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (count == null) {
					count = Long.valueOf(0);
				}

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_G_M, finderArgs,
					count);

				closeSession(session);
			}
		}

		return count.intValue();
	}

	public int countAll() throws SystemException {
		Object[] finderArgs = new Object[0];

		Long count = (Long)FinderCacheUtil.getResult(FINDER_PATH_COUNT_ALL,
				finderArgs, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(_SQL_COUNT_MBSTATSUSER);

				count = (Long)q.uniqueResult();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (count == null) {
					count = Long.valueOf(0);
				}

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_ALL, finderArgs,
					count);

				closeSession(session);
			}
		}

		return count.intValue();
	}

	public void afterPropertiesSet() {
		String[] listenerClassNames = StringUtil.split(GetterUtil.getString(
					com.liferay.portal.util.PropsUtil.get(
						"value.object.listener.com.liferay.portlet.messageboards.model.MBStatsUser")));

		if (listenerClassNames.length > 0) {
			try {
				List<ModelListener<MBStatsUser>> listenersList = new ArrayList<ModelListener<MBStatsUser>>();

				for (String listenerClassName : listenerClassNames) {
					listenersList.add((ModelListener<MBStatsUser>)Class.forName(
							listenerClassName).newInstance());
				}

				listeners = listenersList.toArray(new ModelListener[listenersList.size()]);
			}
			catch (Exception e) {
				_log.error(e);
			}
		}
	}

	@BeanReference(name = "com.liferay.portlet.messageboards.service.persistence.MBBanPersistence")
	protected com.liferay.portlet.messageboards.service.persistence.MBBanPersistence mbBanPersistence;
	@BeanReference(name = "com.liferay.portlet.messageboards.service.persistence.MBCategoryPersistence")
	protected com.liferay.portlet.messageboards.service.persistence.MBCategoryPersistence mbCategoryPersistence;
	@BeanReference(name = "com.liferay.portlet.messageboards.service.persistence.MBDiscussionPersistence")
	protected com.liferay.portlet.messageboards.service.persistence.MBDiscussionPersistence mbDiscussionPersistence;
	@BeanReference(name = "com.liferay.portlet.messageboards.service.persistence.MBMailingListPersistence")
	protected com.liferay.portlet.messageboards.service.persistence.MBMailingListPersistence mbMailingListPersistence;
	@BeanReference(name = "com.liferay.portlet.messageboards.service.persistence.MBMessagePersistence")
	protected com.liferay.portlet.messageboards.service.persistence.MBMessagePersistence mbMessagePersistence;
	@BeanReference(name = "com.liferay.portlet.messageboards.service.persistence.MBMessageFlagPersistence")
	protected com.liferay.portlet.messageboards.service.persistence.MBMessageFlagPersistence mbMessageFlagPersistence;
	@BeanReference(name = "com.liferay.portlet.messageboards.service.persistence.MBStatsUserPersistence")
	protected com.liferay.portlet.messageboards.service.persistence.MBStatsUserPersistence mbStatsUserPersistence;
	@BeanReference(name = "com.liferay.portlet.messageboards.service.persistence.MBThreadPersistence")
	protected com.liferay.portlet.messageboards.service.persistence.MBThreadPersistence mbThreadPersistence;
	@BeanReference(name = "com.liferay.portal.service.persistence.ResourcePersistence")
	protected com.liferay.portal.service.persistence.ResourcePersistence resourcePersistence;
	@BeanReference(name = "com.liferay.portal.service.persistence.UserPersistence")
	protected com.liferay.portal.service.persistence.UserPersistence userPersistence;
	private static final String _SQL_SELECT_MBSTATSUSER = "SELECT mbStatsUser FROM MBStatsUser mbStatsUser";
	private static final String _SQL_SELECT_MBSTATSUSER_WHERE = "SELECT mbStatsUser FROM MBStatsUser mbStatsUser WHERE ";
	private static final String _SQL_COUNT_MBSTATSUSER = "SELECT COUNT(mbStatsUser) FROM MBStatsUser mbStatsUser";
	private static final String _SQL_COUNT_MBSTATSUSER_WHERE = "SELECT COUNT(mbStatsUser) FROM MBStatsUser mbStatsUser WHERE ";
	private static Log _log = LogFactoryUtil.getLog(MBStatsUserPersistenceImpl.class);
}