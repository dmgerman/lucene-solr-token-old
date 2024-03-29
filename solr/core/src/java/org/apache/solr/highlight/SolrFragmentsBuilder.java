begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|highlight
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
operator|.
name|BoundaryScanner
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
operator|.
name|FragmentsBuilder
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
name|HighlightParams
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
name|SolrParams
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
name|util
operator|.
name|plugin
operator|.
name|NamedListInitializedPlugin
import|;
end_import
begin_class
DECL|class|SolrFragmentsBuilder
specifier|public
specifier|abstract
class|class
name|SolrFragmentsBuilder
extends|extends
name|HighlightingPluginBase
implements|implements
name|SolrInfoMBean
implements|,
name|NamedListInitializedPlugin
block|{
DECL|field|DEFAULT_PRE_TAGS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_PRE_TAGS
init|=
literal|"<em>"
decl_stmt|;
DECL|field|DEFAULT_POST_TAGS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_POST_TAGS
init|=
literal|"</em>"
decl_stmt|;
comment|/**    * Return a {@link org.apache.lucene.search.vectorhighlight.FragmentsBuilder} appropriate for this field.    *     * @param params The params controlling Highlighting    * @return An appropriate {@link org.apache.lucene.search.vectorhighlight.FragmentsBuilder}.    */
DECL|method|getFragmentsBuilder
specifier|public
name|FragmentsBuilder
name|getFragmentsBuilder
parameter_list|(
name|SolrParams
name|params
parameter_list|,
name|BoundaryScanner
name|bs
parameter_list|)
block|{
name|numRequests
operator|++
expr_stmt|;
name|params
operator|=
name|SolrParams
operator|.
name|wrapDefaults
argument_list|(
name|params
argument_list|,
name|defaults
argument_list|)
expr_stmt|;
return|return
name|getFragmentsBuilder
argument_list|(
name|params
argument_list|,
name|getPreTags
argument_list|(
name|params
argument_list|,
literal|null
argument_list|)
argument_list|,
name|getPostTags
argument_list|(
name|params
argument_list|,
literal|null
argument_list|)
argument_list|,
name|bs
argument_list|)
return|;
block|}
DECL|method|getPreTags
specifier|public
name|String
index|[]
name|getPreTags
parameter_list|(
name|SolrParams
name|params
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|getTags
argument_list|(
name|params
argument_list|,
name|HighlightParams
operator|.
name|TAG_PRE
argument_list|,
name|fieldName
argument_list|,
name|DEFAULT_PRE_TAGS
argument_list|)
return|;
block|}
DECL|method|getPostTags
specifier|public
name|String
index|[]
name|getPostTags
parameter_list|(
name|SolrParams
name|params
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|getTags
argument_list|(
name|params
argument_list|,
name|HighlightParams
operator|.
name|TAG_POST
argument_list|,
name|fieldName
argument_list|,
name|DEFAULT_POST_TAGS
argument_list|)
return|;
block|}
DECL|method|getTags
specifier|private
name|String
index|[]
name|getTags
parameter_list|(
name|SolrParams
name|params
parameter_list|,
name|String
name|paramName
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|String
name|def
parameter_list|)
block|{
name|params
operator|=
name|SolrParams
operator|.
name|wrapDefaults
argument_list|(
name|params
argument_list|,
name|defaults
argument_list|)
expr_stmt|;
name|String
name|value
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|fieldName
operator|==
literal|null
condition|)
name|value
operator|=
name|params
operator|.
name|get
argument_list|(
name|paramName
argument_list|,
name|def
argument_list|)
expr_stmt|;
else|else
name|value
operator|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|paramName
argument_list|,
name|def
argument_list|)
expr_stmt|;
name|String
index|[]
name|tags
init|=
name|value
operator|.
name|split
argument_list|(
literal|","
argument_list|)
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
name|tags
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|tags
index|[
name|i
index|]
operator|=
name|tags
index|[
name|i
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
return|return
name|tags
return|;
block|}
DECL|method|getFragmentsBuilder
specifier|protected
specifier|abstract
name|FragmentsBuilder
name|getFragmentsBuilder
parameter_list|(
name|SolrParams
name|params
parameter_list|,
name|String
index|[]
name|preTags
parameter_list|,
name|String
index|[]
name|postTags
parameter_list|,
name|BoundaryScanner
name|bs
parameter_list|)
function_decl|;
DECL|method|getMultiValuedSeparatorChar
specifier|protected
name|char
name|getMultiValuedSeparatorChar
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|String
name|separator
init|=
name|params
operator|.
name|get
argument_list|(
name|HighlightParams
operator|.
name|MULTI_VALUED_SEPARATOR
argument_list|,
literal|" "
argument_list|)
decl_stmt|;
if|if
condition|(
name|separator
operator|.
name|length
argument_list|()
operator|>
literal|1
condition|)
block|{
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
name|HighlightParams
operator|.
name|MULTI_VALUED_SEPARATOR
operator|+
literal|" parameter must be a char, but is \""
operator|+
name|separator
operator|+
literal|"\""
argument_list|)
throw|;
block|}
return|return
name|separator
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
return|;
block|}
block|}
end_class
end_unit
