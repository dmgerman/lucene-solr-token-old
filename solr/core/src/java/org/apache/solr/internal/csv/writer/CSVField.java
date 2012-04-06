begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.internal.csv.writer
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|internal
operator|.
name|csv
operator|.
name|writer
package|;
end_package
begin_comment
comment|/**  *   * @author Martin van den Bemt  */
end_comment
begin_class
DECL|class|CSVField
specifier|public
class|class
name|CSVField
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|field|fill
specifier|private
name|int
name|fill
decl_stmt|;
DECL|field|overrideFill
specifier|private
name|boolean
name|overrideFill
decl_stmt|;
comment|/**      *       */
DECL|method|CSVField
specifier|public
name|CSVField
parameter_list|()
block|{     }
comment|/**      * @param name the name of the field      */
DECL|method|CSVField
specifier|public
name|CSVField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param name the name of the field      * @param size the size of the field      */
DECL|method|CSVField
specifier|public
name|CSVField
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|setSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return the name of the field      */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * Set the name of the field      * @param name the name      */
DECL|method|setName
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**      *       * @return the size of the field      */
DECL|method|getSize
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**      * Set the size of the field.      * The size will be ignored when fixedwidth is set to false in the CSVConfig      * @param size the size of the field.      */
DECL|method|setSize
specifier|public
name|void
name|setSize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
comment|/**      * @return the fill pattern.      */
DECL|method|getFill
specifier|public
name|int
name|getFill
parameter_list|()
block|{
return|return
name|fill
return|;
block|}
comment|/**      * Sets overrideFill to true.      * @param fill the file pattern      */
DECL|method|setFill
specifier|public
name|void
name|setFill
parameter_list|(
name|int
name|fill
parameter_list|)
block|{
name|overrideFill
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|fill
operator|=
name|fill
expr_stmt|;
block|}
comment|/**      * Does this field override fill ?      *       */
DECL|method|overrideFill
specifier|public
name|boolean
name|overrideFill
parameter_list|()
block|{
return|return
name|overrideFill
return|;
block|}
block|}
end_class
end_unit
