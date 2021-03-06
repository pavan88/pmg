package com.myt.pmg.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.myt.pmg.model.Link;
import com.myt.pmg.model.UserLink;

@Repository
public class LinkDao extends BasicDaoImpl<Link> {

	private static Logger logger = Logger.getLogger(LinkDao.class);

	public List<Link> findAll() {
		System.out.println("Link Dao");
		final String COLLECTION_NAME = getMongoTemplate().getCollectionName(Link.class);
		return (List<Link>) getMongoTemplate().findAll(Link.class, COLLECTION_NAME);
	}

	public long count() {
		return super.count(Link.class);
	}

	public Link findById(String id) {
		return super.findById(id, Link.class);
	}

	public Link findByLid(String lid) {
		Query query = new Query();
		query.addCriteria(Criteria.where("lid").is(Integer.valueOf(lid)));
		return getMongoTemplate().findOne(query, Link.class);

	}

	public List<Link> findAllLinksPostedToUser(String userid) {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(userid));
		List<UserLink> userlinkList = getMongoTemplate().find(query, UserLink.class);
		List<Link> linkList = new ArrayList<Link>();
		/*
		 * for (UserLink userLink : userlinkList) {
		 * System.out.println(userLink); if (!userLink.isClicked())
		 * linkList.add(findById(userLink.getLinkId())); } for (Link link :
		 * linkList) { System.out.println(link); } //return linkList;
		 */ return null;
	}

	public List<Link> getLinksSortByTime(int n) {
		Query query = new Query();
		query.limit(n);
		query.with(new Sort(Sort.Direction.ASC, "lastTraversedTime"));
		query.addCriteria(Criteria.where("id").exists(true));
		return getMongoTemplate().find(query, Link.class);
	}

	public long countOfLinkPostedByUser(String userId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(userId));
		return getMongoTemplate().count(query, Link.class);
	}

	public long countOfApprovedLinkByUser(String userId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(userId));
		return getMongoTemplate().count(query, Link.class);
	}

	public long countOfDisapprovedLinkByUser(String userId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(userId));
		return getMongoTemplate().count(query, Link.class);
	}

	public List<Link> getActiveLinks() {
		Query query = new Query();
		query.addCriteria(Criteria.where("active").is(true));
		return getMongoTemplate().find(query, Link.class);
	}

	public List<Link> getActiveLinksWithinDuration(int duration) {
		Query query = new Query();
		query.addCriteria(Criteria.where("active").is(true));
		Date date = new Date(0);
		long t = date.getTime();
		Date td = new Date(t - duration * 60000);
		query.with(new Sort(Sort.Direction.ASC, "lastTraversedTime"));
		query.addCriteria(Criteria.where("lastTraversedTime").lt(td));
		query.addCriteria(Criteria.where("id").exists(true));
		return getMongoTemplate().find(query, Link.class);
	}

	public List<Link> findLimitedLinksPostedToUser(String userid, int n) {
		Query query = new Query();
		query.addCriteria(Criteria.where("userid").is(userid));
		query.with(new Sort(Sort.Direction.ASC, "lastTraversedTime"));
		query.limit(n);
		List<UserLink> userlinkList = getMongoTemplate().find(query, UserLink.class);
		if (userlinkList.isEmpty())
			logger.error("userlinklist is empty");

		List<Link> linkList = new ArrayList<Link>();
		for (UserLink userLink : userlinkList) {
			System.out.println(userLink);
			linkList.add(findById(userLink.getLinkId()));
		}
		for (Link link : linkList) {
			System.out.println(link);
		}
		return linkList;
	}

	public void update(Link link) {
		getMongoTemplate().save(link);
	}

	public List<Link> findLinksPostedByUser(String userid) {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(userid));
		return getMongoTemplate().find(query, Link.class);
	}

	public List<Link> findByUserId(String userid) {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(userid));
		/*
		 * query.addCriteria(Criteria.where("clicked").is(false));
		 * query.addCriteria(Criteria.where("verified").is(false));
		 * query.addCriteria(Criteria.where("active").is(true));
		 */
		return getMongoTemplate().find(query, Link.class);
	}

	public boolean isurlexists(String url) {
		Query query = new Query();
		query.addCriteria(Criteria.where("url").is(url));
		if (getMongoTemplate().findOne(query, Link.class) == null)
			return true;
		else
			return false;
		// URL Matches

		// If empty then no url is present
		// return false;
	}

	public Link findByIdAndIsClicked(String linkId) {

		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(linkId));
		query.addCriteria(Criteria.where("linkstatus").is("CLICKED"));

		return getMongoTemplate().findOne(query, Link.class);
	}

	public Link findByIdAndIsClickedAndIsNotApproved(String linkId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(linkId));
		query.addCriteria(Criteria.where("linkstatus").is("CLICKED"));
		query.addCriteria(Criteria.where("isApproved").is(false));

		return getMongoTemplate().findOne(query, Link.class);
	}

}
