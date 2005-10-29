begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|FieldInfo
specifier|final
class|class
name|FieldInfo
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|isIndexed
name|boolean
name|isIndexed
decl_stmt|;
DECL|field|number
name|int
name|number
decl_stmt|;
comment|// true if term vector for this field should be stored
DECL|field|storeTermVector
name|boolean
name|storeTermVector
decl_stmt|;
DECL|field|storeOffsetWithTermVector
name|boolean
name|storeOffsetWithTermVector
decl_stmt|;
DECL|field|storePositionWithTermVector
name|boolean
name|storePositionWithTermVector
decl_stmt|;
DECL|field|omitNorms
name|boolean
name|omitNorms
decl_stmt|;
comment|// omit norms associated with indexed fields
DECL|method|FieldInfo
name|FieldInfo
parameter_list|(
name|String
name|na
parameter_list|,
name|boolean
name|tk
parameter_list|,
name|int
name|nu
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|,
name|boolean
name|storePositionWithTermVector
parameter_list|,
name|boolean
name|storeOffsetWithTermVector
parameter_list|,
name|boolean
name|omitNorms
parameter_list|)
block|{
name|name
operator|=
name|na
expr_stmt|;
name|isIndexed
operator|=
name|tk
expr_stmt|;
name|number
operator|=
name|nu
expr_stmt|;
name|this
operator|.
name|storeTermVector
operator|=
name|storeTermVector
expr_stmt|;
name|this
operator|.
name|storeOffsetWithTermVector
operator|=
name|storeOffsetWithTermVector
expr_stmt|;
name|this
operator|.
name|storePositionWithTermVector
operator|=
name|storePositionWithTermVector
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
name|omitNorms
expr_stmt|;
block|}
block|}
end_class
end_unit
