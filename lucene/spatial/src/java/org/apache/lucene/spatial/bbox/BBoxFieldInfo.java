begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.bbox
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|bbox
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
name|spatial
operator|.
name|SpatialFieldInfo
import|;
end_import
begin_comment
comment|/**  * The Bounding Box gets stored as four fields for x/y min/max and a flag   * that says if the box crosses the dateline (xdl).  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|BBoxFieldInfo
specifier|public
class|class
name|BBoxFieldInfo
implements|implements
name|SpatialFieldInfo
block|{
DECL|field|SUFFIX_MINX
specifier|public
specifier|static
specifier|final
name|String
name|SUFFIX_MINX
init|=
literal|"__minX"
decl_stmt|;
DECL|field|SUFFIX_MAXX
specifier|public
specifier|static
specifier|final
name|String
name|SUFFIX_MAXX
init|=
literal|"__maxX"
decl_stmt|;
DECL|field|SUFFIX_MINY
specifier|public
specifier|static
specifier|final
name|String
name|SUFFIX_MINY
init|=
literal|"__minY"
decl_stmt|;
DECL|field|SUFFIX_MAXY
specifier|public
specifier|static
specifier|final
name|String
name|SUFFIX_MAXY
init|=
literal|"__maxY"
decl_stmt|;
DECL|field|SUFFIX_XDL
specifier|public
specifier|static
specifier|final
name|String
name|SUFFIX_XDL
init|=
literal|"__xdl"
decl_stmt|;
DECL|field|bbox
specifier|public
name|String
name|bbox
init|=
literal|"bbox"
decl_stmt|;
DECL|field|minX
specifier|public
name|String
name|minX
init|=
literal|"bbox.minx"
decl_stmt|;
DECL|field|minY
specifier|public
name|String
name|minY
init|=
literal|"bbox.miny"
decl_stmt|;
DECL|field|maxX
specifier|public
name|String
name|maxX
init|=
literal|"bbox.maxx"
decl_stmt|;
DECL|field|maxY
specifier|public
name|String
name|maxY
init|=
literal|"bbox.maxy"
decl_stmt|;
DECL|field|xdl
specifier|public
name|String
name|xdl
init|=
literal|"bbox.xdl"
decl_stmt|;
comment|// crosses dateline
DECL|method|BBoxFieldInfo
specifier|public
name|BBoxFieldInfo
parameter_list|()
block|{    }
DECL|method|BBoxFieldInfo
specifier|public
name|BBoxFieldInfo
parameter_list|(
name|String
name|p
parameter_list|)
block|{
name|this
operator|.
name|setFieldsPrefix
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
DECL|method|setFieldsPrefix
specifier|public
name|void
name|setFieldsPrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|bbox
operator|=
name|prefix
expr_stmt|;
name|minX
operator|=
name|prefix
operator|+
name|SUFFIX_MINX
expr_stmt|;
name|maxX
operator|=
name|prefix
operator|+
name|SUFFIX_MAXX
expr_stmt|;
name|minY
operator|=
name|prefix
operator|+
name|SUFFIX_MINY
expr_stmt|;
name|maxY
operator|=
name|prefix
operator|+
name|SUFFIX_MAXY
expr_stmt|;
name|xdl
operator|=
name|prefix
operator|+
name|SUFFIX_XDL
expr_stmt|;
block|}
block|}
end_class
end_unit
