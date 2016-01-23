begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|SortField
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
name|search
operator|.
name|function
operator|.
name|ValueSource
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
name|search
operator|.
name|function
operator|.
name|OrdFieldSource
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
name|analysis
operator|.
name|Token
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
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|Tokenizer
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermAttribute
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
name|document
operator|.
name|Fieldable
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
name|TextResponseWriter
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
name|XMLWriter
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
name|analysis
operator|.
name|SolrAnalyzer
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
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * @version $Id$  */
end_comment
begin_class
DECL|class|BoolField
specifier|public
class|class
name|BoolField
extends|extends
name|FieldType
block|{
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{   }
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
return|return
name|getStringSort
argument_list|(
name|field
argument_list|,
name|reverse
argument_list|)
return|;
block|}
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|(
name|SchemaField
name|field
parameter_list|)
block|{
return|return
operator|new
name|OrdFieldSource
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
comment|// avoid instantiating every time...
DECL|field|TRUE_TOKEN
specifier|protected
specifier|final
specifier|static
name|char
index|[]
name|TRUE_TOKEN
init|=
block|{
literal|'T'
block|}
decl_stmt|;
DECL|field|FALSE_TOKEN
specifier|protected
specifier|final
specifier|static
name|char
index|[]
name|FALSE_TOKEN
init|=
block|{
literal|'F'
block|}
decl_stmt|;
comment|////////////////////////////////////////////////////////////////////////
comment|// TODO: look into creating my own queryParser that can more efficiently
comment|// handle single valued non-text fields (int,bool,etc) if needed.
DECL|field|boolAnalyzer
specifier|protected
specifier|final
specifier|static
name|Analyzer
name|boolAnalyzer
init|=
operator|new
name|SolrAnalyzer
argument_list|()
block|{
specifier|public
name|TokenStreamInfo
name|getStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|Tokenizer
argument_list|(
name|reader
argument_list|)
block|{
specifier|final
name|TermAttribute
name|termAtt
init|=
operator|(
name|TermAttribute
operator|)
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|boolean
name|done
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|(
name|Reader
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|done
operator|=
literal|false
expr_stmt|;
name|super
operator|.
name|reset
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
if|if
condition|(
name|done
condition|)
return|return
literal|false
return|;
name|done
operator|=
literal|true
expr_stmt|;
name|int
name|ch
init|=
name|input
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|ch
operator|==
operator|-
literal|1
condition|)
return|return
literal|false
return|;
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
operator|(
operator|(
name|ch
operator|==
literal|'t'
operator|||
name|ch
operator|==
literal|'T'
operator|||
name|ch
operator|==
literal|'1'
operator|)
condition|?
name|TRUE_TOKEN
else|:
name|FALSE_TOKEN
operator|)
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|TokenStreamInfo
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|boolAnalyzer
return|;
block|}
DECL|method|getQueryAnalyzer
specifier|public
name|Analyzer
name|getQueryAnalyzer
parameter_list|()
block|{
return|return
name|boolAnalyzer
return|;
block|}
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
name|char
name|ch
init|=
operator|(
name|val
operator|!=
literal|null
operator|&&
name|val
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
condition|?
name|val
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
else|:
literal|0
decl_stmt|;
return|return
operator|(
name|ch
operator|==
literal|'1'
operator|||
name|ch
operator|==
literal|'t'
operator|||
name|ch
operator|==
literal|'T'
operator|)
condition|?
literal|"T"
else|:
literal|"F"
return|;
block|}
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
return|return
name|indexedToReadable
argument_list|(
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|Boolean
name|toObject
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
return|return
name|Boolean
operator|.
name|valueOf
argument_list|(
name|toExternal
argument_list|(
name|f
argument_list|)
argument_list|)
return|;
block|}
DECL|method|indexedToReadable
specifier|public
name|String
name|indexedToReadable
parameter_list|(
name|String
name|indexedForm
parameter_list|)
block|{
name|char
name|ch
init|=
name|indexedForm
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|ch
operator|==
literal|'T'
condition|?
literal|"true"
else|:
literal|"false"
return|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|XMLWriter
name|xmlWriter
parameter_list|,
name|String
name|name
parameter_list|,
name|Fieldable
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|xmlWriter
operator|.
name|writeBool
argument_list|(
name|name
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'T'
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|TextResponseWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|Fieldable
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|writeBool
argument_list|(
name|name
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'T'
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
