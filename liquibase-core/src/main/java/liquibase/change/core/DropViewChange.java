package liquibase.change.core;

import static liquibase.change.ChangeParameterMetaData.ALL;

import liquibase.change.*;
import liquibase.database.Database;
import liquibase.snapshot.SnapshotGeneratorFactory;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.DropViewStatement;
import liquibase.structure.core.View;
import liquibase.util.ObjectUtil;

/**
 * Drops an existing view.
 */
@DatabaseChange(name="dropView", description = "Drops an existing view", priority = ChangeMetaData.PRIORITY_DEFAULT, appliesTo = "view")
public class DropViewChange extends AbstractChange {
    private String catalogName;
    private String schemaName;
    private String viewName;
    private Boolean ifExists;

    @DatabaseChangeProperty(mustEqualExisting ="view.catalog", since = "3.0")
    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    @DatabaseChangeProperty(mustEqualExisting ="view.schema")
    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    @DatabaseChangeProperty(mustEqualExisting = "view", description = "Name of the view to drop")
    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    @DatabaseChangeProperty(supportsDatabase = ALL,
            description = "Option to only drop the view, if it exists, and not failing, if it's not exists")
    public Boolean isIfExists() {
        return ifExists;
    }

    public void setIfExists(Boolean ifExists) {
        this.ifExists = ifExists;
    }

    @Override
    public SqlStatement[] generateStatements(Database database) {
        return new SqlStatement[]{
                new DropViewStatement(getCatalogName(), getSchemaName(), getViewName(), isIfExists()),
        };
    }

    @Override
    public ChangeStatus checkStatus(Database database) {
        try {
            return new ChangeStatus().assertComplete(!SnapshotGeneratorFactory.getInstance().has(new View(getCatalogName(), getSchemaName(), getViewName()), database), "View exists");
        } catch (Exception e) {
            return new ChangeStatus().unknown(e);
        }
    }


    @Override
    public String getConfirmationMessage() {
        return "View "+getViewName()+" dropped";
    }

    @Override
    public String getSerializedObjectNamespace() {
        return STANDARD_CHANGELOG_NAMESPACE;
    }
}
