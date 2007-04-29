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
name|HashMap
import|;
end_import
begin_comment
comment|/**  * @author yonik  * @version $Id$  */
end_comment
begin_class
DECL|class|FieldProperties
specifier|abstract
class|class
name|FieldProperties
block|{
comment|// use a bitfield instead of many different boolean variables since
comment|// many of the variables are independent or semi-independent.
comment|// bit values for boolean field properties.
DECL|field|INDEXED
specifier|final
specifier|static
name|int
name|INDEXED
init|=
literal|0x00000001
decl_stmt|;
DECL|field|TOKENIZED
specifier|final
specifier|static
name|int
name|TOKENIZED
init|=
literal|0x00000002
decl_stmt|;
DECL|field|STORED
specifier|final
specifier|static
name|int
name|STORED
init|=
literal|0x00000004
decl_stmt|;
DECL|field|BINARY
specifier|final
specifier|static
name|int
name|BINARY
init|=
literal|0x00000008
decl_stmt|;
DECL|field|COMPRESSED
specifier|final
specifier|static
name|int
name|COMPRESSED
init|=
literal|0x00000010
decl_stmt|;
DECL|field|OMIT_NORMS
specifier|final
specifier|static
name|int
name|OMIT_NORMS
init|=
literal|0x00000020
decl_stmt|;
DECL|field|STORE_TERMVECTORS
specifier|final
specifier|static
name|int
name|STORE_TERMVECTORS
init|=
literal|0x00000040
decl_stmt|;
DECL|field|STORE_TERMPOSITIONS
specifier|final
specifier|static
name|int
name|STORE_TERMPOSITIONS
init|=
literal|0x00000080
decl_stmt|;
DECL|field|STORE_TERMOFFSETS
specifier|final
specifier|static
name|int
name|STORE_TERMOFFSETS
init|=
literal|0x00000100
decl_stmt|;
DECL|field|MULTIVALUED
specifier|final
specifier|static
name|int
name|MULTIVALUED
init|=
literal|0x00000200
decl_stmt|;
DECL|field|SORT_MISSING_FIRST
specifier|final
specifier|static
name|int
name|SORT_MISSING_FIRST
init|=
literal|0x00000400
decl_stmt|;
DECL|field|SORT_MISSING_LAST
specifier|final
specifier|static
name|int
name|SORT_MISSING_LAST
init|=
literal|0x00000800
decl_stmt|;
DECL|field|REQUIRED
specifier|final
specifier|static
name|int
name|REQUIRED
init|=
literal|0x00001000
decl_stmt|;
DECL|field|propertyNames
specifier|static
specifier|final
name|String
index|[]
name|propertyNames
init|=
block|{
literal|"indexed"
block|,
literal|"tokenized"
block|,
literal|"stored"
block|,
literal|"binary"
block|,
literal|"compressed"
block|,
literal|"omitNorms"
block|,
literal|"termVectors"
block|,
literal|"termPositions"
block|,
literal|"termOffsets"
block|,
literal|"multiValued"
block|,
literal|"sortMissingFirst"
block|,
literal|"sortMissingLast"
block|,
literal|"required"
block|}
decl_stmt|;
DECL|field|propertyMap
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|propertyMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
for|for
control|(
name|String
name|prop
range|:
name|propertyNames
control|)
block|{
name|propertyMap
operator|.
name|put
argument_list|(
name|prop
argument_list|,
name|propertyNameToInt
argument_list|(
name|prop
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Returns the symbolic name for the property. */
DECL|method|getPropertyName
specifier|static
name|String
name|getPropertyName
parameter_list|(
name|int
name|property
parameter_list|)
block|{
return|return
name|propertyNames
index|[
name|Integer
operator|.
name|numberOfTrailingZeros
argument_list|(
name|property
argument_list|)
index|]
return|;
block|}
DECL|method|propertyNameToInt
specifier|static
name|int
name|propertyNameToInt
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|propertyNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|propertyNames
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|1
operator|<<
name|i
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
DECL|method|propertiesToString
specifier|static
name|String
name|propertiesToString
parameter_list|(
name|int
name|properties
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|properties
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|first
operator|=
literal|false
expr_stmt|;
name|int
name|bitpos
init|=
name|Integer
operator|.
name|numberOfTrailingZeros
argument_list|(
name|properties
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getPropertyName
argument_list|(
literal|1
operator|<<
name|bitpos
argument_list|)
argument_list|)
expr_stmt|;
name|properties
operator|&=
operator|~
operator|(
literal|1
operator|<<
name|bitpos
operator|)
expr_stmt|;
comment|// clear that bit position
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|on
specifier|static
name|boolean
name|on
parameter_list|(
name|int
name|bitfield
parameter_list|,
name|int
name|props
parameter_list|)
block|{
return|return
operator|(
name|bitfield
operator|&
name|props
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|off
specifier|static
name|boolean
name|off
parameter_list|(
name|int
name|bitfield
parameter_list|,
name|int
name|props
parameter_list|)
block|{
return|return
operator|(
name|bitfield
operator|&
name|props
operator|)
operator|==
literal|0
return|;
block|}
comment|/***   static int normalize(int properties) {     int p = properties;     if (on(p,TOKENIZED)&& off(p,INDEXED)) {       throw new RuntimeException("field must be indexed to be tokenized.");     }      if (on(p,STORE_TERMPOSITIONS)) p|=STORE_TERMVECTORS;     if (on(p,STORE_TERMOFFSETS)) p|=STORE_TERMVECTORS;     if (on(p,STORE_TERMOFFSETS)&& off(p,INDEXED)) {       throw new RuntimeException("field must be indexed to store term vectors.");     }      if (on(p,OMIT_NORMS)&& off(p,INDEXED)) {       throw new RuntimeException("field must be indexed for norms to be omitted.");     }      if (on(p,SORT_MISSING_FIRST)&& on(p,SORT_MISSING_LAST)) {       throw new RuntimeException("conflicting options sortMissingFirst,sortMissingLast.");     }      if ((on(p,SORT_MISSING_FIRST) || on(p,SORT_MISSING_LAST))&& off(p,INDEXED)) {       throw new RuntimeException("field must be indexed to be sorted.");     }      if ((on(p,BINARY) || on(p,COMPRESSED))&& off(p,STORED)) {       throw new RuntimeException("field must be stored for compressed or binary options.");     }      return p;   }   ***/
DECL|method|parseProperties
specifier|static
name|int
name|parseProperties
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|,
name|boolean
name|which
parameter_list|)
block|{
name|int
name|props
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|prop
range|:
name|properties
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|propertyMap
operator|.
name|get
argument_list|(
name|prop
argument_list|)
operator|==
literal|null
condition|)
continue|continue;
name|String
name|val
init|=
name|properties
operator|.
name|get
argument_list|(
name|prop
argument_list|)
decl_stmt|;
if|if
condition|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|val
argument_list|)
operator|==
name|which
condition|)
block|{
name|props
operator||=
name|propertyNameToInt
argument_list|(
name|prop
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|props
return|;
block|}
block|}
end_class
end_unit
