begin_unit
begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|RequestHandlerBase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrInfoMBean
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|SolrQueryResponse
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_comment
comment|/**  * A request handler that provides info about all   * registered SolrInfoMBeans.  */
end_comment
begin_class
DECL|class|SolrInfoMBeanHandler
specifier|public
class|class
name|SolrInfoMBeanHandler
extends|extends
name|RequestHandlerBase
block|{
comment|/**    * Take an array of any type and generate a Set containing the toString.    * Set is garunteed to never be null (but may be empty)    */
DECL|method|arrayToSet
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|arrayToSet
parameter_list|(
name|Object
index|[]
name|arr
parameter_list|)
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|r
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|arr
condition|)
return|return
name|r
return|;
for|for
control|(
name|Object
name|o
range|:
name|arr
control|)
block|{
if|if
condition|(
literal|null
operator|!=
name|o
condition|)
name|r
operator|.
name|add
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|req
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|cats
init|=
operator|new
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"solr-mbeans"
argument_list|,
name|cats
argument_list|)
expr_stmt|;
name|String
index|[]
name|requestedCats
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getParams
argument_list|(
literal|"cat"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|requestedCats
operator|||
literal|0
operator|==
name|requestedCats
operator|.
name|length
condition|)
block|{
for|for
control|(
name|SolrInfoMBean
operator|.
name|Category
name|cat
range|:
name|SolrInfoMBean
operator|.
name|Category
operator|.
name|values
argument_list|()
control|)
block|{
name|cats
operator|.
name|add
argument_list|(
name|cat
operator|.
name|name
argument_list|()
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|String
name|catName
range|:
name|requestedCats
control|)
block|{
name|cats
operator|.
name|add
argument_list|(
name|catName
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|requestedKeys
init|=
name|arrayToSet
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getParams
argument_list|(
literal|"key"
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|SolrInfoMBean
argument_list|>
name|reg
init|=
name|core
operator|.
name|getInfoRegistry
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|SolrInfoMBean
argument_list|>
name|entry
range|:
name|reg
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|SolrInfoMBean
name|m
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|requestedKeys
operator|.
name|isEmpty
argument_list|()
operator|||
name|requestedKeys
operator|.
name|contains
argument_list|(
name|key
argument_list|)
operator|)
condition|)
continue|continue;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|catInfo
init|=
name|cats
operator|.
name|get
argument_list|(
name|m
operator|.
name|getCategory
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|catInfo
condition|)
continue|continue;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|mBeanInfo
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|mBeanInfo
operator|.
name|add
argument_list|(
literal|"class"
argument_list|,
name|m
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|mBeanInfo
operator|.
name|add
argument_list|(
literal|"version"
argument_list|,
name|m
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|mBeanInfo
operator|.
name|add
argument_list|(
literal|"description"
argument_list|,
name|m
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|mBeanInfo
operator|.
name|add
argument_list|(
literal|"srcId"
argument_list|,
name|m
operator|.
name|getSourceId
argument_list|()
argument_list|)
expr_stmt|;
name|mBeanInfo
operator|.
name|add
argument_list|(
literal|"src"
argument_list|,
name|m
operator|.
name|getSource
argument_list|()
argument_list|)
expr_stmt|;
name|mBeanInfo
operator|.
name|add
argument_list|(
literal|"docs"
argument_list|,
name|m
operator|.
name|getDocs
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getFieldBool
argument_list|(
name|key
argument_list|,
literal|"stats"
argument_list|,
literal|false
argument_list|)
condition|)
name|mBeanInfo
operator|.
name|add
argument_list|(
literal|"stats"
argument_list|,
name|m
operator|.
name|getStatistics
argument_list|()
argument_list|)
expr_stmt|;
name|catInfo
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|mBeanInfo
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|setHttpCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// never cache, no matter what init config looks like
block|}
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Get Info (and statistics) about all registered SolrInfoMBeans"
return|;
block|}
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
block|}
end_class
end_unit
