/*
 * Appvoat - Do Androids Dream of Electric Goat?
 *
 * This file is licensed under the General Public License version 3 or later.
 * See the COPYING file.
 *
 * @author Maxence Lange <maxence@pontapreta.net>
 * @copyright (C) 2017 Maxence Lange
 * @license GNU GPL version 3 or any later version
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


package net.pr0npaganda.appvoat.api.voat.v1.model;

import java.io.Serializable;


public class RecursiveComments implements Serializable
{
	public int     childCount;
	public String  content;
	public String  creationDate;
	public String  formattedContent;
	public int     id;
	public boolean isAnonymized;
	public boolean isCollapsed;
	public boolean isDeleted;
	public boolean isSaved;
	public boolean isDistinguished;
	public boolean isOwner;
	public boolean isSubmitter;
	public String  lastEditDate;
	public int     parentID;
	public int     submissionID;
	public String  subverse;
	public String  userName;
	public int     vote;
	public int     sum;
	public int     upCount;
	public int     downCount;
	public RecursiveChildren children = new RecursiveChildren();
}