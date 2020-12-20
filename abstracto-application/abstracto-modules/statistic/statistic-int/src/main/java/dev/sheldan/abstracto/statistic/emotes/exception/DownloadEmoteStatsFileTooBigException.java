package dev.sheldan.abstracto.statistic.emotes.exception;

import dev.sheldan.abstracto.core.exception.AbstractoRunTimeException;
import dev.sheldan.abstracto.statistic.emotes.model.exception.DownloadEmoteStatsFileTooBigModel;
import dev.sheldan.abstracto.templating.Templatable;

/**
 * Exception in case the CSV file created by `exportEmoteStats` is larger than the file size limit of the guild it was requested in.
 * TODO: split this up into multiple files (total max), in order to make this exception obsolete
 */
public class DownloadEmoteStatsFileTooBigException extends AbstractoRunTimeException implements Templatable {

    private final DownloadEmoteStatsFileTooBigModel model;

    public DownloadEmoteStatsFileTooBigException(Long fileSize, Long maxFileSize) {
        this.model = DownloadEmoteStatsFileTooBigModel.builder().fileSize(fileSize).fileSizeLimit(maxFileSize).build();
    }

    @Override
    public String getTemplateName() {
        return "emote_stats_download_file_size_too_big";
    }

    @Override
    public Object getTemplateModel() {
        return model;
    }
}