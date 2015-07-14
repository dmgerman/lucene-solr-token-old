begin_unit
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
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
name|params
operator|.
name|CoreAdminParams
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
name|StrUtils
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
name|Utils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|CharArr
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONParser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONWriter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
import|;
end_import
begin_comment
comment|/**  * This class encapsulates the config overlay json file. It is immutable  * and any edit operations performed on tbhis gives a new copy of the object  * with the changed value  */
end_comment
begin_class
DECL|class|ConfigOverlay
specifier|public
class|class
name|ConfigOverlay
implements|implements
name|MapSerializable
block|{
DECL|field|znodeVersion
specifier|private
specifier|final
name|int
name|znodeVersion
decl_stmt|;
DECL|field|data
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|data
decl_stmt|;
DECL|field|props
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
decl_stmt|;
DECL|field|userProps
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|userProps
decl_stmt|;
DECL|method|ConfigOverlay
specifier|public
name|ConfigOverlay
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jsonObj
parameter_list|,
name|int
name|znodeVersion
parameter_list|)
block|{
if|if
condition|(
name|jsonObj
operator|==
literal|null
condition|)
name|jsonObj
operator|=
name|Collections
operator|.
name|EMPTY_MAP
expr_stmt|;
name|this
operator|.
name|znodeVersion
operator|=
name|znodeVersion
expr_stmt|;
name|data
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|jsonObj
argument_list|)
expr_stmt|;
name|props
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|data
operator|.
name|get
argument_list|(
literal|"props"
argument_list|)
expr_stmt|;
if|if
condition|(
name|props
operator|==
literal|null
condition|)
name|props
operator|=
name|Collections
operator|.
name|EMPTY_MAP
expr_stmt|;
name|userProps
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|data
operator|.
name|get
argument_list|(
literal|"userProps"
argument_list|)
expr_stmt|;
if|if
condition|(
name|userProps
operator|==
literal|null
condition|)
name|userProps
operator|=
name|Collections
operator|.
name|EMPTY_MAP
expr_stmt|;
block|}
DECL|method|getXPathProperty
specifier|public
name|Object
name|getXPathProperty
parameter_list|(
name|String
name|xpath
parameter_list|)
block|{
return|return
name|getXPathProperty
argument_list|(
name|xpath
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|getXPathProperty
specifier|public
name|Object
name|getXPathProperty
parameter_list|(
name|String
name|xpath
parameter_list|,
name|boolean
name|onlyPrimitive
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|hierarchy
init|=
name|checkEditable
argument_list|(
name|xpath
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|hierarchy
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|Utils
operator|.
name|getObjectByPath
argument_list|(
name|props
argument_list|,
name|onlyPrimitive
argument_list|,
name|hierarchy
argument_list|)
return|;
block|}
DECL|method|setUserProperty
specifier|public
name|ConfigOverlay
name|setUserProperty
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|val
parameter_list|)
block|{
name|Map
name|copy
init|=
operator|new
name|LinkedHashMap
argument_list|(
name|userProps
argument_list|)
decl_stmt|;
name|copy
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jsonObj
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|this
operator|.
name|data
argument_list|)
decl_stmt|;
name|jsonObj
operator|.
name|put
argument_list|(
literal|"userProps"
argument_list|,
name|copy
argument_list|)
expr_stmt|;
return|return
operator|new
name|ConfigOverlay
argument_list|(
name|jsonObj
argument_list|,
name|znodeVersion
argument_list|)
return|;
block|}
DECL|method|unsetUserProperty
specifier|public
name|ConfigOverlay
name|unsetUserProperty
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
operator|!
name|userProps
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
return|return
name|this
return|;
name|Map
name|copy
init|=
operator|new
name|LinkedHashMap
argument_list|(
name|userProps
argument_list|)
decl_stmt|;
name|copy
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jsonObj
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|this
operator|.
name|data
argument_list|)
decl_stmt|;
name|jsonObj
operator|.
name|put
argument_list|(
literal|"userProps"
argument_list|,
name|copy
argument_list|)
expr_stmt|;
return|return
operator|new
name|ConfigOverlay
argument_list|(
name|jsonObj
argument_list|,
name|znodeVersion
argument_list|)
return|;
block|}
DECL|method|setProperty
specifier|public
name|ConfigOverlay
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|val
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|hierarchy
init|=
name|checkEditable
argument_list|(
name|name
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Map
name|deepCopy
init|=
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSON
argument_list|(
name|Utils
operator|.
name|toJSON
argument_list|(
name|props
argument_list|)
argument_list|)
decl_stmt|;
name|Map
name|obj
init|=
name|deepCopy
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hierarchy
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|hierarchy
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|<
name|hierarchy
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|obj
operator|.
name|get
argument_list|(
name|s
argument_list|)
operator|==
literal|null
operator|||
operator|(
operator|!
operator|(
name|obj
operator|.
name|get
argument_list|(
name|s
argument_list|)
operator|instanceof
name|Map
operator|)
operator|)
condition|)
block|{
name|obj
operator|.
name|put
argument_list|(
name|s
argument_list|,
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|obj
operator|=
operator|(
name|Map
operator|)
name|obj
operator|.
name|get
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|obj
operator|.
name|put
argument_list|(
name|s
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jsonObj
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|this
operator|.
name|data
argument_list|)
decl_stmt|;
name|jsonObj
operator|.
name|put
argument_list|(
literal|"props"
argument_list|,
name|deepCopy
argument_list|)
expr_stmt|;
return|return
operator|new
name|ConfigOverlay
argument_list|(
name|jsonObj
argument_list|,
name|znodeVersion
argument_list|)
return|;
block|}
DECL|field|NOT_EDITABLE
specifier|public
specifier|static
specifier|final
name|String
name|NOT_EDITABLE
init|=
literal|"''{0}'' is not an editable property"
decl_stmt|;
DECL|method|checkEditable
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|checkEditable
parameter_list|(
name|String
name|propName
parameter_list|,
name|boolean
name|isXPath
parameter_list|,
name|boolean
name|failOnError
parameter_list|)
block|{
name|LinkedList
argument_list|<
name|String
argument_list|>
name|hierarchy
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isEditableProp
argument_list|(
name|propName
argument_list|,
name|isXPath
argument_list|,
name|hierarchy
argument_list|)
condition|)
block|{
if|if
condition|(
name|failOnError
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|StrUtils
operator|.
name|formatString
argument_list|(
name|NOT_EDITABLE
argument_list|,
name|propName
argument_list|)
argument_list|)
throw|;
else|else
return|return
literal|null
return|;
block|}
return|return
name|hierarchy
return|;
block|}
DECL|method|unsetProperty
specifier|public
name|ConfigOverlay
name|unsetProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|hierarchy
init|=
name|checkEditable
argument_list|(
name|name
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Map
name|deepCopy
init|=
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSON
argument_list|(
name|Utils
operator|.
name|toJSON
argument_list|(
name|props
argument_list|)
argument_list|)
decl_stmt|;
name|Map
name|obj
init|=
name|deepCopy
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hierarchy
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|hierarchy
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|<
name|hierarchy
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|obj
operator|.
name|get
argument_list|(
name|s
argument_list|)
operator|==
literal|null
operator|||
operator|(
operator|!
operator|(
name|obj
operator|.
name|get
argument_list|(
name|s
argument_list|)
operator|instanceof
name|Map
operator|)
operator|)
condition|)
block|{
return|return
name|this
return|;
block|}
name|obj
operator|=
operator|(
name|Map
operator|)
name|obj
operator|.
name|get
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|obj
operator|.
name|remove
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jsonObj
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|this
operator|.
name|data
argument_list|)
decl_stmt|;
name|jsonObj
operator|.
name|put
argument_list|(
literal|"props"
argument_list|,
name|deepCopy
argument_list|)
expr_stmt|;
return|return
operator|new
name|ConfigOverlay
argument_list|(
name|jsonObj
argument_list|,
name|znodeVersion
argument_list|)
return|;
block|}
DECL|method|toByteArray
specifier|public
name|byte
index|[]
name|toByteArray
parameter_list|()
block|{
return|return
name|Utils
operator|.
name|toJSON
argument_list|(
name|data
argument_list|)
return|;
block|}
DECL|method|getZnodeVersion
specifier|public
name|int
name|getZnodeVersion
parameter_list|()
block|{
return|return
name|znodeVersion
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|CharArr
name|out
init|=
operator|new
name|CharArr
argument_list|()
decl_stmt|;
try|try
block|{
operator|new
name|JSONWriter
argument_list|(
name|out
argument_list|,
literal|2
argument_list|)
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|field|RESOURCE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|RESOURCE_NAME
init|=
literal|"configoverlay.json"
decl_stmt|;
comment|/*private static final Long STR_ATTR = 0L;   private static final Long STR_NODE = 1L;   private static final Long BOOL_ATTR = 10L;   private static final Long BOOL_NODE = 11L;   private static final Long INT_ATTR = 20L;   private static final Long INT_NODE = 21L;   private static final Long FLOAT_ATTR = 30L;   private static final Long FLOAT_NODE = 31L;*/
DECL|field|editable_prop_map
specifier|private
specifier|static
name|Map
name|editable_prop_map
decl_stmt|;
comment|//The path maps to the xml xpath and value of 1 means it is a tag with a string value and value
comment|// of 0 means it is an attribute with string value
DECL|field|MAPPING
specifier|public
specifier|static
specifier|final
name|String
name|MAPPING
init|=
literal|"{"
operator|+
literal|"  updateHandler:{"
operator|+
literal|"    autoCommit:{"
operator|+
literal|"      maxDocs:20,"
operator|+
literal|"      maxTime:20,"
operator|+
literal|"      openSearcher:11},"
operator|+
literal|"    autoSoftCommit:{"
operator|+
literal|"      maxDocs:20,"
operator|+
literal|"      maxTime:20},"
operator|+
literal|"    commitWithin:{softCommit:11},"
operator|+
literal|"    indexWriter:{closeWaitsForMerges:11}},"
operator|+
literal|"  query:{"
operator|+
literal|"    filterCache:{"
operator|+
literal|"      class:0,"
operator|+
literal|"      size:0,"
operator|+
literal|"      initialSize:20,"
operator|+
literal|"      autowarmCount:20,"
operator|+
literal|"      maxRamMB:20,"
operator|+
literal|"      regenerator:0},"
operator|+
literal|"    queryResultCache:{"
operator|+
literal|"      class:0,"
operator|+
literal|"      size:20,"
operator|+
literal|"      initialSize:20,"
operator|+
literal|"      autowarmCount:20,"
operator|+
literal|"      maxRamMB:20,"
operator|+
literal|"      regenerator:0},"
operator|+
literal|"    documentCache:{"
operator|+
literal|"      class:0,"
operator|+
literal|"      size:20,"
operator|+
literal|"      initialSize:20,"
operator|+
literal|"      autowarmCount:20,"
operator|+
literal|"      regenerator:0},"
operator|+
literal|"    fieldValueCache:{"
operator|+
literal|"      class:0,"
operator|+
literal|"      size:20,"
operator|+
literal|"      initialSize:20,"
operator|+
literal|"      autowarmCount:20,"
operator|+
literal|"      regenerator:0},"
operator|+
literal|"    useFilterForSortedQuery:1,"
operator|+
literal|"    queryResultWindowSize:1,"
operator|+
literal|"    queryResultMaxDocsCached:1,"
operator|+
literal|"    enableLazyFieldLoading:1,"
operator|+
literal|"    boolTofilterOptimizer:1,"
operator|+
literal|"    maxBooleanClauses:1},"
operator|+
literal|"  jmx:{"
operator|+
literal|"    agentId:0,"
operator|+
literal|"    serviceUrl:0,"
operator|+
literal|"    rootName:0},"
operator|+
literal|"  requestDispatcher:{"
operator|+
literal|"    handleSelect:0,"
operator|+
literal|"    requestParsers:{"
operator|+
literal|"      multipartUploadLimitInKB:0,"
operator|+
literal|"      formdataUploadLimitInKB:0,"
operator|+
literal|"      enableRemoteStreaming:0,"
operator|+
literal|"      addHttpRequestToContext:0}}}"
decl_stmt|;
static|static
block|{
try|try
block|{
name|editable_prop_map
operator|=
operator|(
name|Map
operator|)
operator|new
name|ObjectBuilder
argument_list|(
operator|new
name|JSONParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|MAPPING
argument_list|)
argument_list|)
argument_list|)
operator|.
name|getObject
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"error parsing mapping "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|isEditableProp
specifier|public
specifier|static
name|boolean
name|isEditableProp
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|isXpath
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|hierarchy
parameter_list|)
block|{
return|return
operator|!
operator|(
name|checkEditable
argument_list|(
name|path
argument_list|,
name|isXpath
argument_list|,
name|hierarchy
argument_list|)
operator|==
literal|null
operator|)
return|;
block|}
DECL|method|checkEditable
specifier|public
specifier|static
name|Class
name|checkEditable
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|isXpath
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|hierarchy
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|parts
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|path
argument_list|,
name|isXpath
condition|?
literal|'/'
else|:
literal|'.'
argument_list|)
decl_stmt|;
name|Object
name|obj
init|=
name|editable_prop_map
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|parts
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|part
init|=
name|parts
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|boolean
name|isAttr
init|=
name|isXpath
operator|&&
name|part
operator|.
name|startsWith
argument_list|(
literal|"@"
argument_list|)
decl_stmt|;
if|if
condition|(
name|isAttr
condition|)
block|{
name|part
operator|=
name|part
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hierarchy
operator|!=
literal|null
condition|)
name|hierarchy
operator|.
name|add
argument_list|(
name|part
argument_list|)
expr_stmt|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|i
operator|==
name|parts
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|obj
decl_stmt|;
name|Object
name|o
init|=
name|map
operator|.
name|get
argument_list|(
name|part
argument_list|)
decl_stmt|;
return|return
name|checkType
argument_list|(
name|o
argument_list|,
name|isXpath
argument_list|,
name|isAttr
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
name|obj
operator|=
operator|(
operator|(
name|Map
operator|)
name|obj
operator|)
operator|.
name|get
argument_list|(
name|part
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
DECL|field|types
specifier|static
name|Class
index|[]
name|types
init|=
operator|new
name|Class
index|[]
block|{
name|String
operator|.
name|class
block|,
name|Boolean
operator|.
name|class
block|,
name|Integer
operator|.
name|class
block|,
name|Float
operator|.
name|class
block|}
decl_stmt|;
DECL|method|checkType
specifier|private
specifier|static
name|Class
name|checkType
parameter_list|(
name|Object
name|o
parameter_list|,
name|boolean
name|isXpath
parameter_list|,
name|boolean
name|isAttr
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Long
condition|)
block|{
name|Long
name|aLong
init|=
operator|(
name|Long
operator|)
name|o
decl_stmt|;
name|int
name|ten
init|=
name|aLong
operator|.
name|intValue
argument_list|()
operator|/
literal|10
decl_stmt|;
name|int
name|one
init|=
name|aLong
operator|.
name|intValue
argument_list|()
operator|%
literal|10
decl_stmt|;
if|if
condition|(
name|isXpath
operator|&&
name|isAttr
operator|&&
name|one
operator|!=
literal|0
condition|)
return|return
literal|null
return|;
return|return
name|types
index|[
name|ten
index|]
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|getEditableSubProperties
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getEditableSubProperties
parameter_list|(
name|String
name|xpath
parameter_list|)
block|{
name|Object
name|o
init|=
name|Utils
operator|.
name|getObjectByPath
argument_list|(
name|props
argument_list|,
literal|false
argument_list|,
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|xpath
argument_list|,
literal|'/'
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|Map
condition|)
block|{
return|return
operator|(
name|Map
operator|)
name|o
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|getUserProps
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getUserProps
parameter_list|()
block|{
return|return
name|userProps
return|;
block|}
annotation|@
name|Override
DECL|method|toMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|toMap
parameter_list|()
block|{
name|Map
name|result
init|=
operator|new
name|LinkedHashMap
argument_list|()
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
name|ZNODEVER
argument_list|,
name|znodeVersion
argument_list|)
expr_stmt|;
name|result
operator|.
name|putAll
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|getNamedPlugins
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|>
name|getNamedPlugins
parameter_list|(
name|String
name|typ
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|>
name|reqHandlers
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|>
operator|)
name|data
operator|.
name|get
argument_list|(
name|typ
argument_list|)
decl_stmt|;
if|if
condition|(
name|reqHandlers
operator|==
literal|null
condition|)
return|return
name|Collections
operator|.
name|EMPTY_MAP
return|;
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|reqHandlers
argument_list|)
return|;
block|}
DECL|method|addNamedPlugin
specifier|public
name|ConfigOverlay
name|addNamedPlugin
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|info
parameter_list|,
name|String
name|typ
parameter_list|)
block|{
name|Map
name|dataCopy
init|=
name|Utils
operator|.
name|getDeepCopy
argument_list|(
name|data
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|Map
name|reqHandler
init|=
operator|(
name|Map
operator|)
name|dataCopy
operator|.
name|get
argument_list|(
name|typ
argument_list|)
decl_stmt|;
if|if
condition|(
name|reqHandler
operator|==
literal|null
condition|)
name|dataCopy
operator|.
name|put
argument_list|(
name|typ
argument_list|,
name|reqHandler
operator|=
operator|new
name|LinkedHashMap
argument_list|()
argument_list|)
expr_stmt|;
name|reqHandler
operator|.
name|put
argument_list|(
name|info
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|NAME
argument_list|)
argument_list|,
name|info
argument_list|)
expr_stmt|;
return|return
operator|new
name|ConfigOverlay
argument_list|(
name|dataCopy
argument_list|,
name|this
operator|.
name|znodeVersion
argument_list|)
return|;
block|}
DECL|method|deleteNamedPlugin
specifier|public
name|ConfigOverlay
name|deleteNamedPlugin
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|typ
parameter_list|)
block|{
name|Map
name|dataCopy
init|=
name|Utils
operator|.
name|getDeepCopy
argument_list|(
name|data
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|Map
name|reqHandler
init|=
operator|(
name|Map
operator|)
name|dataCopy
operator|.
name|get
argument_list|(
name|typ
argument_list|)
decl_stmt|;
if|if
condition|(
name|reqHandler
operator|==
literal|null
condition|)
return|return
name|this
return|;
name|reqHandler
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
operator|new
name|ConfigOverlay
argument_list|(
name|dataCopy
argument_list|,
name|this
operator|.
name|znodeVersion
argument_list|)
return|;
block|}
DECL|field|ZNODEVER
specifier|public
specifier|static
specifier|final
name|String
name|ZNODEVER
init|=
literal|"znodeVersion"
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"overlay"
decl_stmt|;
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{   }
block|}
end_class
end_unit
