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
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|Document
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
name|store
operator|.
name|RAMDirectory
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
name|store
operator|.
name|OutputStream
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
comment|//import org.cnlp.utils.properties.ResourceBundleHelper;
end_comment
begin_class
DECL|class|TestFieldInfos
specifier|public
class|class
name|TestFieldInfos
extends|extends
name|TestCase
block|{
DECL|field|testDoc
specifier|private
name|Document
name|testDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
DECL|method|TestFieldInfos
specifier|public
name|TestFieldInfos
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|testDoc
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{   }
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
comment|//Positive test of FieldInfos
name|assertTrue
argument_list|(
name|testDoc
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|FieldInfos
name|fieldInfos
init|=
operator|new
name|FieldInfos
argument_list|()
decl_stmt|;
name|fieldInfos
operator|.
name|add
argument_list|(
name|testDoc
argument_list|)
expr_stmt|;
comment|//Since the complement is stored as well in the fields map
name|assertTrue
argument_list|(
name|fieldInfos
operator|.
name|size
argument_list|()
operator|==
literal|7
argument_list|)
expr_stmt|;
comment|//this is 7 b/c we are using the no-arg constructor
name|RAMDirectory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|String
name|name
init|=
literal|"testFile"
decl_stmt|;
name|OutputStream
name|output
init|=
name|dir
operator|.
name|createFile
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|output
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//Use a RAMOutputStream
try|try
block|{
name|fieldInfos
operator|.
name|write
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|output
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|FieldInfos
name|readIn
init|=
operator|new
name|FieldInfos
argument_list|(
name|dir
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fieldInfos
operator|.
name|size
argument_list|()
operator|==
name|readIn
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|FieldInfo
name|info
init|=
name|readIn
operator|.
name|fieldInfo
argument_list|(
literal|"textField1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|info
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|storeTermVector
operator|==
literal|false
argument_list|)
expr_stmt|;
name|info
operator|=
name|readIn
operator|.
name|fieldInfo
argument_list|(
literal|"textField2"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|storeTermVector
operator|==
literal|true
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
